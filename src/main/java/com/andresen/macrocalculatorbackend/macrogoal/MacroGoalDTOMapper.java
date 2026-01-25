package com.andresen.macrocalculatorbackend.macrogoal;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class MacroGoalDTOMapper implements Function<MacroGoal, MacroGoalDTO> {

    @Override
    public MacroGoalDTO apply(MacroGoal macroGoal) {
        return new MacroGoalDTO(
                macroGoal.getId(),
                macroGoal.getCalorieTarget(),
                macroGoal.getProteinG(),
                macroGoal.getCarbsG(),
                macroGoal.getFatG(),
                macroGoal.isActive()
        );
    }
}
