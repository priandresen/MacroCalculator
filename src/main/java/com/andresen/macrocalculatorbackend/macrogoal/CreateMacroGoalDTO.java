package com.andresen.macrocalculatorbackend.macrogoal;

import com.andresen.macrocalculatorbackend.userprofile.UserProfile;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateMacroGoalDTO(

        Long id,
        @NotNull UserProfile userProfile,
        @NotNull @Positive Integer calorieTarget,
        @NotNull @Positive Integer proteinG,
        @NotNull @Positive Integer carbsG,
        @NotNull @Positive Integer fatG,
        @NotNull boolean isActive
        ) {


}
