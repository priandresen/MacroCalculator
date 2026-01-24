package com.andresen.macrocalculatorbackend.userprofile;

import com.andresen.macrocalculatorbackend.exception.ResourceNotFoundException;
import com.andresen.macrocalculatorbackend.shared.ActivityLevel;
import com.andresen.macrocalculatorbackend.shared.Goal;
import com.andresen.macrocalculatorbackend.shared.Intensity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    public List<UserProfileDTO> getAllUserProfiles() {
        return userProfileRepository
                .findAll()
                .stream()
                .map(userProfileDTOMapper)
                .toList();
    }

    public UserProfileDTO insertUserProfile(CreateUserProfileDTO request) {

        ActivityLevel activityLevel = request.activityLevel();
        Goal goal = request.goal();
        Intensity intensity = request.intensity();
        LocalDate dateOfBirth = request.dateOfBirth();

        UserProfile userProfile = new UserProfile(
                request.name(),
                request.sex(),
                dateOfBirth,
                request.weightKg(),
                request.heightCm(),
                activityLevel,
                goal,
                intensity,
                request.bodyFatPercentage()
        );

        UserProfile saved = userProfileRepository.save(userProfile);

        return userProfileDTOMapper.apply(saved);
    }

    public UserProfileDTO getUserProfileById(Long id) {
        return userProfileRepository.findById(id)
                .map(userProfileDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException
                        (
                                "user with id [%s] not found".formatted(id)
                        ));
    }

    public UserProfileDTO updateUserProfile(Long id, CreateUserProfileDTO request) {
        UserProfile existing = userProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user with id [%s] not found".formatted(id)));

        existing.setName(request.name());
        existing.setSex(request.sex());
        existing.setDateOfBirth(request.dateOfBirth());
        existing.setWeightKg(request.weightKg());
        existing.setHeightCm(request.heightCm());
        existing.setActivityLevel(request.activityLevel());
        existing.setGoal(request.goal());
        existing.setIntensity(request.intensity());
        existing.setBodyFatPercentage(request.bodyFatPercentage());

        UserProfile saved = userProfileRepository.save(existing);
        return userProfileDTOMapper.apply(saved);
    }


}
