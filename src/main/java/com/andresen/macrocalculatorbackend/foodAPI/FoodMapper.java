package com.andresen.macrocalculatorbackend.foodAPI;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FoodMapper {

    public FoodSearchResultDTO toSearchResult(UsdaFoodItemDTO food) {
        return new FoodSearchResultDTO(
                food.fdcId(),
                food.description(),
                food.brandName(),
                food.dataType(),
                food.servingSize(),
                food.servingSizeUnit(),
                findCalories(food.foodNutrients())
        );
    }

    public FoodDetailsDTO toFoodDetails(UsdaFoodDetailsResponse food) {
        return new FoodDetailsDTO(
                food.description(),
                findNutrient(food, "Energy"),
                findNutrient(food, "Protein"),
                findNutrient(food, "Carbohydrate"),
                findNutrient(food, "Total lipid"),
                food.servingSize(),
                food.servingSizeUnit()
        );
    }

    private Double findNutrient(UsdaFoodDetailsResponse food, String name) {
        List<UsdaNutrientDTO> nutrients = food.foodNutrients();
        if (nutrients == null) return null;

        return nutrients.stream()
                .filter(n -> n != null && n.nutrient() != null && n.nutrient().name() != null)
                .filter(n -> n.nutrient().name().toLowerCase().contains(name.toLowerCase()))
                .map(UsdaNutrientDTO::amount)
                .findFirst()
                .orElse(null);
    }

    private Double findCalories(UsdaFoodDetailsResponse food) {
        return findCalories(food.foodNutrients());
    }

    private Double findCalories(List<UsdaNutrientDTO> nutrients) {
        if (nutrients == null) return null;

        return nutrients.stream()
                .filter(n -> n != null && n.nutrient() != null && n.nutrient().id() != null)
                .filter(n -> n.nutrient().id().equals(1008L)) // Energy (kcal)
                .map(UsdaNutrientDTO::amount)
                .findFirst()
                .orElse(null);
    }

}
