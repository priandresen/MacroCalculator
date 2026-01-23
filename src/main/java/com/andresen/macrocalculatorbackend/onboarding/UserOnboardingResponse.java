package com.andresen.macrocalculatorbackend.onboarding;

import com.andresen.macrocalculatorbackend.macrogoal.MacroGoal;
import com.andresen.macrocalculatorbackend.userprofile.UserProfileDTO;

public record UserOnboardingResponse(
        UserProfileDTO userProfile,
        MacroGoal activeMacroGoal
) {}
