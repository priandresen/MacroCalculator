package com.andresen.macrocalculatorbackend.userprofile;

import com.andresen.macrocalculatorbackend.shared.ActivityLevel;
import com.andresen.macrocalculatorbackend.shared.Goal;
import com.andresen.macrocalculatorbackend.shared.Intensity;
import com.andresen.macrocalculatorbackend.shared.Sex;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateUserProfileDTO(
        @Size(min = 1, max = 100) String name,
        Sex sex,
        @Past LocalDate dateOfBirth,
        @Positive Double weightKg,
        @Positive Double heightCm,
        ActivityLevel activityLevel,
        Goal goal,
        Intensity intensity,
        @Positive Double bodyFatPercentage

) {}
