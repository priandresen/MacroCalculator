package com.andresen.macrocalculatorbackend.macrogoal;

import com.andresen.macrocalculatorbackend.exception.ResourceNotFoundException;
import com.andresen.macrocalculatorbackend.userprofile.UserProfile;
import com.andresen.macrocalculatorbackend.userprofile.UserProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class MacroGoalService {

    private final UserProfileRepository userProfileRepository;
    private final MacroGoalRepository macroGoalRepository;

    public MacroGoalService(
            UserProfileRepository userProfileRepository,
            MacroGoalRepository macroGoalRepository
    ) {
        this.userProfileRepository = userProfileRepository;
        this.macroGoalRepository = macroGoalRepository;
    }

    public MacroGoal createMacroGoal(Long userProfileId, CreateMacroGoalDTO request) {
        UserProfile userProfile = userProfileRepository
                .findById(userProfileId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User profile not found")
                );

        MacroGoal macroGoal = new MacroGoal(
                userProfile,
                request.calorieTarget(),
                request.proteinG(),
                request.carbsG(),
                request.fatG(),
                request.isActive()
        );

        return macroGoalRepository.save(macroGoal);
    }
}
