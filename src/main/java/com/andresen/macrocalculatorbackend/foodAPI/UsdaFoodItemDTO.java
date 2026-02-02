package com.andresen.macrocalculatorbackend.foodAPI;

import java.util.List;

public record UsdaFoodItemDTO(
        Long fdcId,
        String description,
        String brandName,
        String dataType,
        Double servingSize,
        String servingSizeUnit,
        List<UsdaNutrientDTO> foodNutrients,
        LabelNutrients labelNutrients
) {

    public record LabelNutrients(
            LabelValue calories
    ) {}

    public record LabelValue(
            Double value
    ) {}
}