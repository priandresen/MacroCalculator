package com.andresen.macrocalculatorbackend;

public record UserProfileDTO(

        Long id,
        String name,
        Double weightGrams,
        Double heightCm,
        ActivityLevel activityLevel,
        Double bodyFatPercentage
) {
}
