package com.andresen.macrocalculatorbackend.userprofile;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserProfileDTOMapper implements Function<UserProfile, UserProfileDTO> {

    @Override
    public UserProfileDTO apply(UserProfile userProfile) {
        return new UserProfileDTO(
                userProfile.getId(),
                userProfile.getName(),
                userProfile.getWeightGrams(),
                userProfile.getHeightCm(),
                userProfile.getActivityLevel(),
                userProfile.getGoal(),
                userProfile.getBodyFatPercentage()
        );
    }
}
