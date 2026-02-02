package com.andresen.macrocalculatorbackend.logs;

public record AddFoodRequest(
        Long fdcId,
        Double servingSize
) {}
