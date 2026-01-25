package com.andresen.macrocalculatorbackend.onboarding;

import com.andresen.macrocalculatorbackend.macrogoal.MacroGoalDTO;
import com.andresen.macrocalculatorbackend.userprofile.UserProfileDTO;

public record UserOnboardingResponse(
        UserProfileDTO userProfile,
        MacroGoalDTO activeMacroGoal
) {}
