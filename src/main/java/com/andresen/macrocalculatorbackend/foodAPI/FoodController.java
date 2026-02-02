package com.andresen.macrocalculatorbackend.foodAPI;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
public class FoodController {

    private final FoodService service;

    public FoodController(FoodService service) {
        this.service = service;
    }

    @GetMapping("/search")
    public List<FoodSearchResultDTO> search(@RequestParam String query) {
        return service.searchFoods(query);
    }

    @GetMapping("search/{foodId}")
    public FoodDetailsDTO getDetails(@PathVariable Long foodId) {
        return service.getFoodDetails(foodId);
    }
}
