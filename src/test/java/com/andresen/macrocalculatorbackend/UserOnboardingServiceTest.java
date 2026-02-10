package com.andresen.macrocalculatorbackend;

import com.andresen.macrocalculatorbackend.macrogoal.MacroGoalDTO;
import com.andresen.macrocalculatorbackend.macrogoal.MacroGoalService;
import com.andresen.macrocalculatorbackend.onboarding.UserOnboardingResponse;
import com.andresen.macrocalculatorbackend.onboarding.UserOnboardingService;
import com.andresen.macrocalculatorbackend.shared.ActivityLevel;
import com.andresen.macrocalculatorbackend.shared.Goal;
import com.andresen.macrocalculatorbackend.shared.Intensity;
import com.andresen.macrocalculatorbackend.shared.Sex;
import com.andresen.macrocalculatorbackend.userprofile.CreateUserProfileDTO;
import com.andresen.macrocalculatorbackend.userprofile.UserProfileDTO;
import com.andresen.macrocalculatorbackend.userprofile.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserOnboardingServiceTest {

    private UserProfileService userProfileService;
    private MacroGoalService macroGoalService;
    private UserOnboardingService service;

    @BeforeEach
    void setUp() {
        userProfileService = mock(UserProfileService.class);
        macroGoalService = mock(MacroGoalService.class);
        service = new UserOnboardingService(userProfileService, macroGoalService);
    }

    @Test
    void createProfileWithActiveMacroGoal_callsBothServices_andReturnsResponse() {
        CreateUserProfileDTO request = new CreateUserProfileDTO(
                "Priscilla",
                LocalDate.of(1995, 1, 1),
                Sex.FEMALE,
                70.0,
                165.0,
                ActivityLevel.MODERATE,
                Goal.MAINTAIN,
                Intensity.INTENSE,
                0.25
        );

        UserProfileDTO createdProfile = new UserProfileDTO(
                10L, "Priscilla", Sex.FEMALE, LocalDate.of(1995, 1, 1),
                70.0, 165.0, ActivityLevel.MODERATE, Goal.MAINTAIN, Intensity.INTENSE, 0.25
        );

        MacroGoalDTO activeGoal = new MacroGoalDTO(99L, 2000, 140, 200, 60, true);

        when(userProfileService.insertUserProfile(request)).thenReturn(createdProfile);
        when(macroGoalService.recalculateActiveGoalFromProfile(10L)).thenReturn(activeGoal);

        UserOnboardingResponse response = service.createProfileWithActiveMacroGoal(request);

        assertEquals(10L, response.userProfile().id());
        assertEquals(99L, response.activeMacroGoal().id());

        verify(userProfileService).insertUserProfile(request);
        verify(macroGoalService).recalculateActiveGoalFromProfile(10L);
    }
}
