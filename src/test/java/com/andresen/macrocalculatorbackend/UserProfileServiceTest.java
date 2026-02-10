package com.andresen.macrocalculatorbackend;

import com.andresen.macrocalculatorbackend.exception.ResourceNotFoundException;
import com.andresen.macrocalculatorbackend.shared.ActivityLevel;
import com.andresen.macrocalculatorbackend.shared.Goal;
import com.andresen.macrocalculatorbackend.shared.Intensity;
import com.andresen.macrocalculatorbackend.shared.Sex;
import com.andresen.macrocalculatorbackend.userprofile.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserProfileServiceTest {

    private UserProfileRepository repo;
    private UserProfileDTOMapper mapper;
    private UserProfileService service;

    @BeforeEach
    void setUp() {
        repo = mock(UserProfileRepository.class);
        mapper = new UserProfileDTOMapper();
        service = new UserProfileService(repo, mapper);
    }

    @Test
    void insertUserProfile_savesEntity_andReturnsDTO() {
        CreateUserProfileDTO request = new CreateUserProfileDTO(
                "Priscilla",
                LocalDate.of(1995, 1, 1),
                Sex.FEMALE,
                70.0,
                165.0,
                ActivityLevel.MODERATE,
                Goal.MAINTAIN,
                Intensity.RECOMMENDED,
                0.25
        );

        // return the same entity instance
        when(repo.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        UserProfileDTO result = service.insertUserProfile(request);

        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);
        verify(repo).save(captor.capture());

        UserProfile saved = captor.getValue();
        assertEquals("Priscilla", saved.getName());
        assertEquals(Sex.FEMALE, saved.getSex());
        assertEquals(70.0, saved.getWeightKg());
        assertEquals(ActivityLevel.MODERATE, saved.getActivityLevel());

        // (id null in unit test since no JPA)
        assertNull(result.id());
        assertEquals("Priscilla", result.name());
        assertEquals(Sex.FEMALE, result.sex());
    }

    @Test
    void patchUserProfile_onlyUpdatesProvidedFields() {
        UserProfile existing = new UserProfile(
                "Old Name",
                Sex.FEMALE,
                LocalDate.of(1990, 1, 1),
                60.0,
                160.0,
                ActivityLevel.SEDENTARY,
                Goal.MAINTAIN,
                Intensity.RECOMMENDED,
                0.20
        );

        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateUserProfileDTO patch = new UpdateUserProfileDTO(
                "New Name",      // update
                null,            // keep
                null,            // keep
                65.0,            // update
                null,            // keep
                null,            // keep
                null,            // keep
                null,            // keep
                null             // keep
        );

        UserProfileDTO updated = service.patchUserProfile(1L, patch);

        assertEquals("New Name", updated.name());
        assertEquals(65.0, updated.weightKg());
        assertEquals(160.0, updated.heightCm()); // unchanged
        verify(repo).save(existing);
    }

    @Test
    void patchUserProfile_throwsWhenUserNotFound() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        UpdateUserProfileDTO patch = new UpdateUserProfileDTO(
                "Name", null, null, null, null, null, null, null, null
        );

        assertThrows(ResourceNotFoundException.class,
                () -> service.patchUserProfile(999L, patch));
    }
}
