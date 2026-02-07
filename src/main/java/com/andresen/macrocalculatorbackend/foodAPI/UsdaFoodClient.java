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
                                .queryParam("dataType", "Branded")
                                .queryParam("pageSize", 200)
                                .queryParam("pageNumber", 1, 2, 3)
                                .queryParam("api_key", config.getApiKey())
                                .queryParam("requireAllWords", false)
//                                .queryParam("sortBy", "dataType.keyword")
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
