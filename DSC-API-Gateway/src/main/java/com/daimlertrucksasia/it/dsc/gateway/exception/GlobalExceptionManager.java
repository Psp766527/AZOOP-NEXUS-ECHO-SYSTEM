package com.daimlertrucksasia.it.dsc.gateway.exception;

import com.daimlertrucksasia.it.dsc.gateway.exception.dto.ErrorResponse;
import com.daimlertrucksasia.it.dsc.gateway.validation.rulebook.UniqueConstraintValidator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.server.reactive.ServerHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

/**
 * Global exception handler for REST controllers.
 * <p>
 * This class provides centralized exception handling for all controllers, converting exceptions
 * into structured {@link ErrorResponse} objects with appropriate HTTP status codes.
 * </p>
 *
 * @since 1.0
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionManager {

    private final UniqueConstraintValidator uniqueValidator;

    /**
     * Constructs a new {@code GlobalExceptionManager} with the required dependencies.
     *
     * @param uniqueValidator validator used to parse database constraint violations into user-friendly messages
     */
    public GlobalExceptionManager(UniqueConstraintValidator uniqueValidator) {
        this.uniqueValidator = uniqueValidator;
    }

    /**
     * Handles {@link InputFieldNoLongerSupported} exceptions thrown when a field is deprecated or no longer supported.
     * <p>
     * Returns a {@code 410 Gone} response indicating that the input field should no longer be used.
     * </p>
     * <p>
     * <b>TODO [v1.2.0]</b>: Add support for internationalization (i18n/l10n) and dynamic message extraction.
     * </p>
     *
     * @param ex      the thrown exception
     * @param request the incoming HTTP request
     * @return a structured {@link ResponseEntity} with HTTP 410 and error details
     */
    @ExceptionHandler(InputFieldNoLongerSupported.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedField(InputFieldNoLongerSupported ex, ServerHttpRequest request) {
        String message = ex.getMessage();
        return buildErrorResponse(HttpStatus.GONE, message, ex, request);
    }

    /**
     * Handles any uncaught exceptions in the application.
     * <p>
     * Returns a {@code 500 Internal Server Error} response to indicate an unexpected failure.
     * </p>
     * <p>
     * <b>TODO [v1.2.0]</b>: Improve this by localizing error messages and dynamically extracting relevant data.
     * </p>
     *
     * @param ex      the uncaught exception
     * @param request the current HTTP request
     * @return a structured {@link ResponseEntity} with HTTP 500 and generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUnhandled(Exception ex, ServerHttpRequest request) {
        log.error("Unhandled exception", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", ex, request);
    }

    /**
     * Handles {@link DataIntegrityViolationException} for database-level unique constraint violations.
     * <p>
     * Returns a {@code 409 Conflict} response indicating that a duplicate record was detected.
     * Extracts a friendly error message using {@link UniqueConstraintValidator}.
     * </p>
     * <p>
     * <b>TODO [v1.2.0]</b>: Refactor for localization and dynamic metadata-driven messages.
     * </p>
     *
     * @param ex      the exception indicating a unique constraint violation
     * @param request the current HTTP request
     * @return a structured {@link ResponseEntity} with HTTP 409 and extracted error message
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateKey(DataIntegrityViolationException ex, ServerHttpRequest request) {
       // String message = uniqueValidator.extractFriendlyMessage(ex);
        String message = ex.getMessage();
        return buildErrorResponse(HttpStatus.CONFLICT, message, ex, request);
    }

    /**
     * Builds a standardized {@link ErrorResponse} object and wraps it in a {@link ResponseEntity}.
     *
     * @param status  the HTTP status code to return
     * @param message a custom error message
     * @param ex      the caught exception (used for logging or debugging)
     * @param request the current HTTP request
     * @return a {@link ResponseEntity} containing the formatted error response
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, Exception ex, ServerHttpRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getURI().getPath())
                .build();

        return new ResponseEntity<>(error, status);
    }
}
