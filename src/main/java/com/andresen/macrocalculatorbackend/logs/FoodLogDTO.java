package com.andresen.macrocalculatorbackend.logs;

public record FoodLogDTO(
        Long id,
        Long usdaId,
        String name,
        String brand,
        Double servingSize,
        String servingUnit,
        Integer calories,
        Integer proteinG,
        Integer carbsG,
        Integer fatG
) {}
