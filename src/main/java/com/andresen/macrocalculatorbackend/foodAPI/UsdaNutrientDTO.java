package com.andresen.macrocalculatorbackend.foodAPI;

public record UsdaNutrientDTO(
        Nutrient nutrient,
        Double amount
) {
    public record Nutrient(
            Long id,
            String name,
            String unitName
    ) {}
}