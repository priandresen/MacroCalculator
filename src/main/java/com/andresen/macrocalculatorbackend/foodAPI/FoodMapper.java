package com.andresen.macrocalculatorbackend.foodAPI;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FoodMapper {

    private static final Long ENERGY_KCAL_ID = 1008L;


    public FoodSearchResultDTO toSearchResult(UsdaFoodItemDTO food) {
        return new FoodSearchResultDTO(
                food.fdcId(),
                food.description(),
                food.brandName(),
                food.dataType(),
                food.servingSize(),
                food.servingSizeUnit(),
                findCalories(food)
        );
    }

    public FoodDetailsDTO toFoodDetails(UsdaFoodDetailsResponse food) {
        return new FoodDetailsDTO(
                food.description(),
                food.brandName(),
                findCalories(food),
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

        if (food.labelNutrients() != null &&
                food.labelNutrients().calories() != null &&
                food.labelNutrients().calories().value() != null) {

            return food.labelNutrients().calories().value();
        }

        return findCalories(food.foodNutrients());
    }

    private Double findCalories(UsdaFoodItemDTO food) {

        if (food.labelNutrients() != null &&
                food.labelNutrients().calories() != null &&
                food.labelNutrients().calories().value() != null) {

            return food.labelNutrients().calories().value();
        }

        return findCalories(food.foodNutrients());
    }

    private Double findCalories(List<UsdaNutrientDTO> nutrients) {
        if (nutrients == null) return null;

        return nutrients.stream()
                .filter(n -> n != null && n.nutrient() != null)
                .filter(n -> ENERGY_KCAL_ID.equals(n.nutrient().id()))
                .map(UsdaNutrientDTO::amount)
                .findFirst()
                .orElse(null);
    }

}
