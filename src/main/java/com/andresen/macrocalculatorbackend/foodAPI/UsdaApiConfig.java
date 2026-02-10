package com.andresen.macrocalculatorbackend.foodAPI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UsdaApiConfig {

    @Value("${usda.api.base}")
    private String baseUrl;

    @Value("${usda.api.key}")
    private String apiKey;

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }
}
