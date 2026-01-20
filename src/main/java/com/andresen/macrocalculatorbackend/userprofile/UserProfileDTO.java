package com.andresen.macrocalculatorbackend.userprofile;

public record UserProfileDTO(

        Long id,
        String name,
        Double weightGrams,
        Double heightCm,
        ActivityLevel activityLevel,
        Goal goal,
        Double bodyFatPercentage
) {
}
