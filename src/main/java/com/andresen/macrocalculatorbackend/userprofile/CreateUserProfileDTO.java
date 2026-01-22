package com.andresen.macrocalculatorbackend.userprofile;

import jakarta.validation.constraints.*;

public record CreateUserProfileDTO(
        @NotBlank(message = "Name is required")
        @Pattern(regexp = ".*\\p{L}.*", message = "Name must contain at least one letter.")
        String name,

        @NotNull(message = "Weight is required")
        @Positive(message = "Weight must be greater than 0")
        Double weightGrams,

        @NotNull(message = "Height is required")
        @Positive(message = "Height must be greater than 0")
        Double heightCm,

        @NotNull(message = "Activity level is required")
        ActivityLevel activityLevel,

        @NotNull(message = "Goal is required")
        Goal goal,


        @DecimalMin(value = "0.0", message = "Body fat % must be at least 0")
        @DecimalMax(value = "1.0", message = "Body fat % must be at most 1.0")
        Double bodyFatPercentage
) {}
