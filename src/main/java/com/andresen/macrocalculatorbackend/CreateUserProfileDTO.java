package com.andresen.macrocalculatorbackend;

public record CreateUserProfileDTO(
        String name,
        Double weightGrams,
        Double heightCm,
        String activityLevel,
        Double bodyFatPercentage
)
{
}
