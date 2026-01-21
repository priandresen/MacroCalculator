package com.andresen.macrocalculatorbackend.userprofile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UserProfileDTO(

        Long id,
        @NotBlank String name,
        @NotNull @Positive Double weightGrams,
        @NotNull @Positive Double heightCm,
        @NotNull ActivityLevel activityLevel,
        @NotNull Goal goal,
        Double bodyFatPercentage
) {
}