package com.andresen.macrocalculatorbackend.logs;

import java.time.LocalDate;
import java.util.List;

public record DailyLogDTO(
        Long id,
        Long userProfileId,
        LocalDate logDate,
        List<FoodLogDTO> foodLogs,
        Integer totalCalories,
        Integer totalProteinG,
        Integer totalCarbsG,
        Integer totalFatG
) {}
