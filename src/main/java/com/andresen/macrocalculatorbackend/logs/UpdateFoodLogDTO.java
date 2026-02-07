package com.andresen.macrocalculatorbackend.logs;

import jakarta.validation.constraints.Positive;

public record UpdateFoodLogDTO(
        @Positive(message = "servingSize must be > 0")
        Double servingSize) {
}
