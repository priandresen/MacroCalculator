package com.andresen.macrocalculatorbackend.onboarding;

import com.andresen.macrocalculatorbackend.macrogoal.MacroGoalDTO;
import com.andresen.macrocalculatorbackend.macrogoal.MacroGoalService;
import com.andresen.macrocalculatorbackend.userprofile.CreateUserProfileDTO;
import com.andresen.macrocalculatorbackend.userprofile.UserProfileDTO;
import com.andresen.macrocalculatorbackend.userprofile.UserProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserOnboardingService {

    private final UserProfileService userProfileService;
    private final MacroGoalService macroGoalService;

    public UserOnboardingService(
            UserProfileService userProfileService,
            MacroGoalService macroGoalService
    ) {
        this.userProfileService = userProfileService;
        this.macroGoalService = macroGoalService;
    }

    /**
     * Creates the user profile AND ensures an active macro goal exists immediately.
     * All-or-nothing: if macro goal creation fails, the profile should not be left created.
     */
    @Transactional
    public UserOnboardingResponse createProfileWithActiveMacroGoal(CreateUserProfileDTO request) {
        UserProfileDTO createdProfile = userProfileService.insertUserProfile(request);

        Long userProfileId = createdProfile.id();

        MacroGoalDTO activeGoal = macroGoalService.recalculateActiveGoalFromProfile(userProfileId);

        return new UserOnboardingResponse(createdProfile, activeGoal);
    }

    /**
     * If you later add profile updates, this becomes the one place that keeps profile + goal in sync.
     * (You’ll need a UserProfileService.update method.)
     */
    @Transactional
    public UserOnboardingResponse updateProfileAndRecalculateActiveGoal(
            Long userProfileId,
            CreateUserProfileDTO request
    ) {
        UserProfileDTO updatedProfile = userProfileService.updateUserProfile(userProfileId, request);
        MacroGoalDTO activeGoal = macroGoalService.recalculateActiveGoalFromProfile(userProfileId);
        return new UserOnboardingResponse(updatedProfile, activeGoal);
    }

}
