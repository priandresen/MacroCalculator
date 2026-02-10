package com.andresen.macrocalculatorbackend;

import com.andresen.macrocalculatorbackend.exception.ResourceNotFoundException;
import com.andresen.macrocalculatorbackend.macrogoal.*;
import com.andresen.macrocalculatorbackend.shared.ActivityLevel;
import com.andresen.macrocalculatorbackend.shared.Goal;
import com.andresen.macrocalculatorbackend.shared.Intensity;
import com.andresen.macrocalculatorbackend.shared.Sex;
import com.andresen.macrocalculatorbackend.userprofile.UserProfile;
import com.andresen.macrocalculatorbackend.userprofile.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MacroGoalServiceTest {

    private UserProfileRepository userRepo;
    private MacroGoalRepository goalRepo;
    private MacroGoalDTOMapper mapper;
    private MacroGoalService service;

    @BeforeEach
    void setUp() {
        userRepo = mock(UserProfileRepository.class);
        goalRepo = mock(MacroGoalRepository.class);
        mapper = new MacroGoalDTOMapper();
        service = new MacroGoalService(userRepo, goalRepo, mapper);
    }

    @Test
    void recalculateActiveGoalFromProfile_deactivatesOldActive_andCreatesNewActive() {
        UserProfile user = new UserProfile(
                "Priscilla",
                Sex.FEMALE,
                LocalDate.now().minusYears(30),
                70.0,
                165.0,
                ActivityLevel.MODERATE,
                Goal.MAINTAIN,
                Intensity.RECOMMENDED,
                0.25
        );

        MacroGoal oldActive = new MacroGoal(user, 2000, 140, 200, 60, true);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(goalRepo.findByUserProfile_IdAndIsActiveTrue(1L)).thenReturn(Optional.of(oldActive));

        when(goalRepo.saveAndFlush(any(MacroGoal.class))).thenAnswer(inv -> inv.getArgument(0));
        when(goalRepo.save(any(MacroGoal.class))).thenAnswer(inv -> inv.getArgument(0));

        MacroGoalDTO newActiveDto = service.recalculateActiveGoalFromProfile(1L);

        // old is deactivated
        assertFalse(oldActive.isActive());
        verify(goalRepo).saveAndFlush(oldActive);

        // new active is created + saved
        ArgumentCaptor<MacroGoal> newGoalCaptor = ArgumentCaptor.forClass(MacroGoal.class);
        verify(goalRepo).save(newGoalCaptor.capture());

        MacroGoal newGoal = newGoalCaptor.getValue();
        assertTrue(newGoal.isActive());
        assertNotNull(newGoal.getCalorieTarget());
        assertNotNull(newGoal.getProteinG());
        assertNotNull(newGoal.getCarbsG());
        assertNotNull(newGoal.getFatG());

        // returned DTO is active and positive
        assertTrue(newActiveDto.isActive());
        assertTrue(newActiveDto.calorieTarget() > 0);
        assertTrue(newActiveDto.proteinG() > 0);
        assertTrue(newActiveDto.fatG() > 0);
    }

    @Test
    void recalculateActiveGoalFromProfile_throwsWhenUserMissing() {
        when(userRepo.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.recalculateActiveGoalFromProfile(999L));
    }
}
