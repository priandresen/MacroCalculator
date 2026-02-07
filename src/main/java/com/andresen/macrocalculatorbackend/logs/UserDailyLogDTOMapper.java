package com.andresen.macrocalculatorbackend.logs;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDailyLogDTOMapper {

    public DailyLogDTO toDTO(UserDailyLog log) {
        List<FoodLogDTO> foods = log.getFoodLogs().stream()
                .map(this::toFoodDTO)
                .toList();

        int totalCalories = 0;
        int totalProtein = 0;
        int totalCarbs = 0;
        int totalFat = 0;

        for (FoodLog f : log.getFoodLogs()) {
            totalCalories += safe(f.getCalories());
            totalProtein  += safe(f.getProteinG());
            totalCarbs    += safe(f.getCarbsG());
            totalFat      += safe(f.getFatG());
        }

        return new DailyLogDTO(
                log.getId(),
                log.getUserProfile().getId(),
                log.getLogDate(),
                foods,
                totalCalories,
                totalProtein,
                totalCarbs,
                totalFat
        );
    }

    public FoodLogDTO toFoodDTO(FoodLog f) {
        return new FoodLogDTO(
                f.getId(),
                f.getUsdaId(),
                f.getName(),
                f.getBrand(),
                f.getServingSize(),
                f.getServingUnit(),
                f.getCalories(),
                f.getProteinG(),
                f.getCarbsG(),
                f.getFatG()
        );
    }

    private int safe(Integer value) {
        return value == null ? 0 : value;
    }
}
