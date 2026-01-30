package com.andresen.macrocalculatorbackend.foodAPI;

public record FoodSearchResultDTO(
        Long foodId,
        String name,
        String brand,
        String source,
        Double servingSize,
        String servingUnit,
        Double calories
) {}
