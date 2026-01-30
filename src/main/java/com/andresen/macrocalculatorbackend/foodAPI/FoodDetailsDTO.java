package com.andresen.macrocalculatorbackend.foodAPI;

public record FoodDetailsDTO(
        String name,
        Double calories,
        Double protein,
        Double carbs,
        Double fat,
        Double servingSize,
        String servingUnit
) {}
