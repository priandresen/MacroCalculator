package com.andresen.macrocalculatorbackend.userprofile;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UserProfileDTO(

        Long id,
        @NotBlank String name,
        @NotNull @Past LocalDate dateOfBirth,
        @NotNull @Positive Double weightGrams,
        @NotNull @Positive Double heightCm,
        @NotNull ActivityLevel activityLevel,
        @NotNull Goal goal,
        Double bodyFatPercentage
) {
}