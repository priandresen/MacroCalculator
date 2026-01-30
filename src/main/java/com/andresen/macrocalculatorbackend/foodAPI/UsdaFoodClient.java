package com.andresen.macrocalculatorbackend.foodAPI;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class UsdaFoodClient {

    private final RestClient restClient;
    private final UsdaApiConfig config;

    public UsdaFoodClient(UsdaApiConfig config) {
        this.config = config;
        this.restClient = RestClient.builder()
                .baseUrl(config.getBaseUrl())
                .build();
    }

    public UsdaFoodSearchResponse searchFoods(String query) {
        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/foods/search")
                                .queryParam("query", query)
                                .queryParam("pageSize", 25)
                                .queryParam("api_key", config.getApiKey())
                                .build()
                )
                .retrieve()
                .body(UsdaFoodSearchResponse.class);
    }

    public UsdaFoodDetailsResponse getFoodDetails(Long fdcId) {
        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/food/{fdcId}")
                                .queryParam("api_key", config.getApiKey())
                                .build(fdcId)
                )
                .retrieve()
                .body(UsdaFoodDetailsResponse.class);
    }

}
