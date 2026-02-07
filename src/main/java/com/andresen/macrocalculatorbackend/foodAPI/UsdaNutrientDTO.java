package com.andresen.macrocalculatorbackend.foodAPI;

public record UsdaNutrientDTO(
        // details endpoint shape
        Nutrient nutrient,
        Double amount,

        // search endpoint shape
        Long nutrientId,
        String nutrientName,
        String unitName,
        Double value
) {

    public record Nutrient(
            Long id,
            String name,
            String unitName
    ) {}

    public Long id() {
        if (nutrient != null && nutrient.id() != null) return nutrient.id();
        return nutrientId;
    }

    public String name() {
        if (nutrient != null && nutrient.name() != null) return nutrient.name();
        return nutrientName;
    }

    public Double amountValue() {
        if (amount != null) return amount;
        return value;
    }
}
