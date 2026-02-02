package com.andresen.macrocalculatorbackend.foodAPI;

public record FoodDetailsDTO(
        String name,
        String brand,
        Double calories,
        Double protein,
        Double carbs,
        Double fat,
        Double servingSize,
        String servingUnit
) {}
