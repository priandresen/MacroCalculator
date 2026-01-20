package com.andresen.macrocalculatorbackend.userprofile;

public record CreateUserProfileDTO(
        String name,
        Double weightGrams,
        Double heightCm,
        String activityLevel,
        String goal,
        Double bodyFatPercentage
)
{
}
