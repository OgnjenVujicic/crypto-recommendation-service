package com.example.cryptorecommendation.rest;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 *
 * @param errorType        Error type.
 * @param message          Message with error information.
 * @param validationErrors Validation errors (optional).
 * @param httpCode         Http code.
 * @param timestamp        Instant (date and time) of error.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        ErrorType errorType,
        String message,
        List<ValidationError> validationErrors,
        Integer httpCode,
        Instant timestamp) {

    public enum ErrorType {
        GENERAL,
        NOT_FOUND,
        VALIDATION_ERROR,
    }

    /**
     * Field validation error.
     *
     * @param field   Field name.
     * @param message Message with more information on validation error.
     */
    public record ValidationError(String field, String message) {
    }
}
