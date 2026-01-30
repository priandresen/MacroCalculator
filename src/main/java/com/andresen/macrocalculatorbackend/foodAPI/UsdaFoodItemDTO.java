package com.andresen.macrocalculatorbackend.foodAPI;

import java.util.List;

public record UsdaFoodItemDTO(
        Long fdcId,
        String description,
        String brandName,
        String dataType,
        Double servingSize,
        String servingSizeUnit,
        List<UsdaNutrientDTO> foodNutrients

) {}
