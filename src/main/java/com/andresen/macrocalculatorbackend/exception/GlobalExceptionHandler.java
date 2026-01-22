
package com.andresen.macrocalculatorbackend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 - request validation errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {

        FieldError fe = ex.getBindingResult().getFieldError();

        // Can happen in edge cases (e.g., only global errors)
        if (fe == null) {
            ApiError body = ApiError.basic(
                    HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    "Validation failed",
                    req.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }

        ApiError body = ApiError.withFieldError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed",
                req.getRequestURI(),
                fe.getField(),
                fe.getDefaultMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        String message = "Invalid request body.";
        String field = null;
        String fieldError = null;

        Throwable cause = ex.getCause();
        System.out.println(cause);
        System.out.println(cause.getClass());

        if (cause instanceof InvalidFormatException ife) {
            field = lastPathFieldName(ife);

            Class<?> targetType = ife.getTargetType();
            if (targetType != null && targetType.isEnum()) {
                String allowed = enumAllowedValues(targetType);
                message = "Invalid enum value.";
                fieldError = (field != null)
                        ? "Allowed values: " + allowed
                        : "Allowed values: " + allowed;
            } else {
                message = "Invalid value type.";
                fieldError = (field != null)
                        ? "Invalid value for " + field + "."
                        : "Invalid value.";
            }
        } else if (cause instanceof MismatchedInputException mie) {
            field = lastPathFieldName(mie);
            message = "Invalid value type.";
            fieldError = (field != null)
                    ? "Invalid value for " + field + "."
                    : "Invalid value.";
        }

        ApiError body = (field == null)
                ? ApiError.basic(400, "Bad Request", message, req.getRequestURI())
                : ApiError.withFieldError(400, "Bad Request", message, req.getRequestURI(), field, fieldError);

        return ResponseEntity.badRequest().body(body);
    }

    private String enumAllowedValues(Class<?> enumType) {
        Object[] constants = enumType.getEnumConstants();
        if (constants == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < constants.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(constants[i].toString());
        }
        return sb.toString();
    }


    // 400 - bad input (your own checks)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalInput(IllegalArgumentException ex, HttpServletRequest req) {
        ApiError body = ApiError.basic(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 404 - not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        ApiError body = ApiError.basic(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // 409 - database constraint violations
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {

        String msg = "Request conflicts with existing data.";
        String mostSpecificMessage = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : null;

        if (mostSpecificMessage != null) {
            if (mostSpecificMessage.contains("macro_goal_one_active_per_user")) {
                msg = "User already has an active macro goal. Deactivate the current one before creating a new active goal.";
            }
        }

        ApiError body = ApiError.basic(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                msg,
                req.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // 500 - safety net
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest req) {
        // Production: log ex with stacktrace
        ApiError body = ApiError.basic(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Unexpected server error",
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    /**
     * Extract the last JSON path segment name from Jackson exceptions.
     * Works for nested objects too.
     */
    private String lastPathFieldName(MismatchedInputException ex) {
        if (ex.getPath() == null || ex.getPath().isEmpty()) return null;

        var last = ex.getPath().get(ex.getPath().size() - 1);

        // For JSON objects: "activityLevel"
        String prop = last.getPropertyName();
        if (prop != null && !prop.isBlank()) return prop;

        // For JSON arrays: index-based (no property name)
        int idx = last.getIndex();
        return (idx >= 0) ? String.valueOf(idx) : null;
    }

}
