package com.andresen.macrocalculatorbackend.foodAPI;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodService {

    private final UsdaFoodClient usdaFoodClient;
    private final FoodMapper mapper;

    public FoodService(UsdaFoodClient usdaFoodClient, FoodMapper mapper) {
        this.usdaFoodClient = usdaFoodClient;
        this.mapper = mapper;
    }

    public List<FoodSearchResultDTO> searchFoods(String query) {
        return usdaFoodClient.searchFoods(query)
                .foods()
                .stream()
                .map(mapper::toSearchResult)
                .toList();
    }

    public FoodDetailsDTO getFoodDetails(Long foodId) {
        return mapper.toFoodDetails(
                usdaFoodClient.getFoodDetails(foodId)
        );
    }
}

