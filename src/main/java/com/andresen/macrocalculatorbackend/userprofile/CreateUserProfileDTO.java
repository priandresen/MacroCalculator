package com.andresen.macrocalculatorbackend.userprofile;

import com.andresen.macrocalculatorbackend.shared.ActivityLevel;
import com.andresen.macrocalculatorbackend.shared.Goal;
import com.andresen.macrocalculatorbackend.shared.Intensity;
import com.andresen.macrocalculatorbackend.shared.Sex;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateUserProfileDTO(
        @NotBlank(message = "Name is required")
        @Pattern(regexp = ".*\\p{L}.*", message = "Name must contain at least one letter.")
        String name,

        @NotNull(message = "Date of birth is required 'YYYY-MM-DD'")
        @Past(message = "date must be in the past")
        LocalDate dateOfBirth,

        @NotNull(message = "Sex is required")
        Sex sex,

        @NotNull(message = "Weight is required")
        @Positive(message = "Weight must be greater than 0")
        Double weightKg,

        @NotNull(message = "Height is required")
        @Positive(message = "Height must be greater than 0")
        Double heightCm,

        @NotNull(message = "Activity level is required")
        ActivityLevel activityLevel,

        @NotNull(message = "Goal is required")
        Goal goal,

        @NotNull(message = "Intensitiy is required")
        Intensity intensity,

        @DecimalMin(value = "0.0", message = "Body fat % must be at least 0")
        @DecimalMax(value = "1.0", message = "Body fat % must be at most 1.0")
        Double bodyFatPercentage
) {}
