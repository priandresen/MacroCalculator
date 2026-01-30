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

    // Keep your "one-field" API shape, but make behavior consistent & easy to extend.
    private static final String VALIDATION_FAILED = "Validation failed";
    private static final String INVALID_REQUEST_BODY = "Invalid request body.";

    // 400 - request validation errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        FieldError fe = ex.getBindingResult().getFieldError();

        if (fe == null) {
            return respondBasic(HttpStatus.BAD_REQUEST, VALIDATION_FAILED, req);
        }

        return respondField(
                HttpStatus.BAD_REQUEST,
                VALIDATION_FAILED,
                req,
                fe.getField(),
                fe.getDefaultMessage()
        );
    }
//            FieldError fieldError = ex.getBindingResult().getFieldError();
//
//            ApiError error = ApiError.withFieldError(
//                    HttpStatus.BAD_REQUEST.value(),
//                    "Bad Request",
//                    "Validation failed",
//                    req.getRequestURI(),
//                    fieldError != null ? fieldError.getField() : null,
//                    fieldError != null ? fieldError.getDefaultMessage() : null
//            );
//
//            return ResponseEntity.badRequest().body(error);
//        }

    // 400 - unreadable JSON / bad types / enum values
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        JsonProblem problem = extractJsonProblem(ex);

        if (problem.field() == null) {
            return respondBasic(HttpStatus.BAD_REQUEST, problem.message(), req);
        }

        return respondField(
                HttpStatus.BAD_REQUEST,
                problem.message(),
                req,
                problem.field(),
                problem.fieldError()
        );
    }

    // 400 - bad input
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalInput(IllegalArgumentException ex, HttpServletRequest req) {
        // In production, you might want to normalize messages here (avoid leaking internals).
        return respondBasic(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    // 404 - not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return respondBasic(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    // 409 - database constraint violations
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        String msg = resolveIntegrityMessage(ex);
        return respondBasic(HttpStatus.CONFLICT, msg, req);
    }

    // 500 - safety net
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest req) {
        // Production: log ex with stacktrace + request id (if you have one).
        return respondBasic(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", req);
    }

    // -------------------------
    // Response builders
    // -------------------------

    private ResponseEntity<ApiError> respondBasic(HttpStatus status, String message, HttpServletRequest req) {
        ApiError body = ApiError.basic(
                status.value(),
                status.getReasonPhrase(),
                message,
                req.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }

    private ResponseEntity<ApiError> respondField(HttpStatus status, String message, HttpServletRequest req, String field, String fieldError) {
        ApiError body = ApiError.withFieldError(
                status.value(),
                status.getReasonPhrase(),
                message,
                req.getRequestURI(),
                field,
                fieldError
        );
        return ResponseEntity.status(status).body(body);
    }

    // -------------------------
    // JSON parsing helpers
    // -------------------------

    private JsonProblem extractJsonProblem(HttpMessageNotReadableException ex) {
        // Default: unknown/unreadable JSON
        String message = INVALID_REQUEST_BODY;
        String field = null;
        String fieldError = null;

        Throwable cause = ex.getCause();
        if (cause == null) {
            return new JsonProblem(message, field, fieldError);
        }

        // Wrong type / invalid enum / invalid value
        if (cause instanceof InvalidFormatException ife) {
            field = lastPathFieldName(ife);
            return jsonProblemFromInvalidFormat(ife, field);
        }

        // Missing fields / wrong shape / type mismatch
        if (cause instanceof MismatchedInputException mie) {
            field = lastPathFieldName(mie);
            message = "Invalid value type.";
            fieldError = (field != null) ? ("Invalid value for " + field + ".") : "Invalid value.";
            return new JsonProblem(message, field, fieldError);
        }

        // Any other parsing exception: keep default
        return new JsonProblem(message, field, fieldError);
    }

    private JsonProblem jsonProblemFromInvalidFormat(InvalidFormatException ife, String field) {
        Class<?> targetType = ife.getTargetType();

        // Enum value that doesn't exist
        if (targetType != null && targetType.isEnum()) {
            String allowed = enumAllowedValues(targetType);
            String message = "Invalid enum value.";
            String fieldError = "Allowed values: " + allowed;
            return new JsonProblem(message, field, fieldError);
        }

        // Other type mismatch (e.g., string into number)
        String message = "Invalid value type.";
        String fieldError = (field != null) ? ("Invalid value for " + field + ".") : "Invalid value.";
        return new JsonProblem(message, field, fieldError);
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

    /**
     * Extract the last JSON path segment name from Jackson exceptions.
     * Works for nested objects too.
     */
    private String lastPathFieldName(MismatchedInputException ex) {
        if (ex.getPath() == null || ex.getPath().isEmpty()) return null;

        var last = ex.getPath().getLast();

        // For JSON objects: "activityLevel"
        String prop = last.getPropertyName();
        if (prop != null && !prop.isBlank()) return prop;

        // For JSON arrays: index-based (no property name)
        int idx = last.getIndex();
        return (idx >= 0) ? String.valueOf(idx) : null;
    }

    // -------------------------
    // DataIntegrity helpers
    // -------------------------

    private String resolveIntegrityMessage(DataIntegrityViolationException ex) {
        // Default safe message
        String msg = "Request conflicts with existing data.";

        String mostSpecificMessage =
                (ex.getMostSpecificCause() != null) ? ex.getMostSpecificCause().getMessage() : null;

        if (mostSpecificMessage == null) return msg;

        // Small “translation table” pattern (easy to extend)
        if (mostSpecificMessage.contains("macro_goal_one_active_per_user")) {
            return "User already has an active macro goal. Deactivate the current one before creating a new active goal.";
        }

        return msg;
    }

    // Small internal carrier for the JSON problem details (keeps handler readable)
    private record JsonProblem(String message, String field, String fieldError) {}


    @ExceptionHandler(FoodMacrosNotFoundException.class)
    public ResponseEntity<ApiError> handleFoodMacrosNotFound(
            FoodMacrosNotFoundException ex,
            HttpServletRequest req
    ) {
        return respondBasic(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                req
        );
    }

    @ExceptionHandler(UnsupportedFoodTypeException.class)
    public ResponseEntity<ApiError> handleUnsupportedFoodType(
            UnsupportedFoodTypeException ex,
            HttpServletRequest req
    ) {
        return respondBasic(
                HttpStatus.valueOf(422),
                ex.getMessage(),
                req
        );
    }

}
