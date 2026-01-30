package com.andresen.macrocalculatorbackend.foodAPI;

import java.util.List;

public record UsdaFoodSearchResponse(
        List<UsdaFoodItemDTO> foods
) {}
