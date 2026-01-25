package com.andresen.macrocalculatorbackend.macrogoal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MacroGoalDTO (

    Long id,
    @NotNull @Positive Integer calorieTarget,
    @NotNull @Positive Integer proteinG,
    @NotNull @Positive Integer carbsG,
    @NotNull @Positive Integer fatG,
    @NotNull boolean isActive
)
{}
