package com.andresen.macrocalculatorbackend.userprofile;

import com.andresen.macrocalculatorbackend.shared.ActivityLevel;
import com.andresen.macrocalculatorbackend.shared.Goal;
import com.andresen.macrocalculatorbackend.shared.Intensity;
import com.andresen.macrocalculatorbackend.shared.Sex;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UserProfileDTO(

        Long id,
        @NotBlank String name,
        @NotNull Sex sex,
        @NotNull @Past LocalDate dateOfBirth,
        @NotNull @Positive Double weightKg,
        @NotNull @Positive Double heightCm,
        @NotNull ActivityLevel activityLevel,
        @NotNull Goal goal,
        @NotNull Intensity intensity,
        Double bodyFatPercentage
) {
}