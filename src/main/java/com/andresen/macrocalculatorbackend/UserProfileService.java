package com.andresen.macrocalculatorbackend;

import org.springframework.stereotype.Service;

import java.util.List;

//this makes UserProfile available to use within other classes

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileDTOMapper userProfileDTOMapper;

    public UserProfileService(
            UserProfileRepository userProfileRepository,
            UserProfileDTOMapper userProfileDTOMapper
    ) {
        this.userProfileRepository = userProfileRepository;
        this.userProfileDTOMapper = userProfileDTOMapper;
    }

    public List<UserProfileDTO> getAllUserProfiles(){
        return userProfileRepository
                .findAll()
                .stream()
                .map(userProfileDTOMapper)
                .toList();
    }

    public UserProfileDTO insertUserProfile(CreateUserProfileDTO request) {

        ActivityLevel activityLevel = ActivityLevel.valueOf(request.activityLevel());

        UserProfile userProfile = new UserProfile(
                request.name(),
                request.weightGrams(),
                request.heightCm(),
                activityLevel,
                request.bodyFatPercentage()
        );

        UserProfile saved = userProfileRepository.save(userProfile);

        return userProfileDTOMapper.apply(saved);
    }

    public UserProfileDTO getUserProfileById(Long id) {
        return userProfileRepository.findById(id)
                .map(userProfileDTOMapper)
                .orElseThrow(() -> new IllegalArgumentException(
                        "user with id [%s] not found".formatted(id)
                ));
    }
}
