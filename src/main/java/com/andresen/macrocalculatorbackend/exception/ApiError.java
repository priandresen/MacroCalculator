package com.andresen.macrocalculatorbackend.exception;

import java.time.Instant;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        String field,
        String fieldError
) {
    public static ApiError basic(int status, String error, String message, String path) {
        return new ApiError(Instant.now(), status, error, message, path, null, null);
    }

    public static ApiError withFieldError(
            int status,
            String error,
            String message,
            String path,
            String field,
            String fieldError
    ) {
        return new ApiError(Instant.now(), status, error, message, path, field, fieldError);
    }
}
