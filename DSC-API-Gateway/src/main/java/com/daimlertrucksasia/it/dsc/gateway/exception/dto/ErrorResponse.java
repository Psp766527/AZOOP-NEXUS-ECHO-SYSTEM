package com.daimlertrucksasia.it.dsc.gateway.exception.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Represents a structured error response returned to the client when an exception occurs.
 * <p>
 * This class is typically used in conjunction with {@code @RestControllerAdvice}
 * to return consistent and informative error details in API responses.
 * </p>
 *
 * <p>Fields include:
 * <ul>
 *     <li>{@code timestamp} - the time the error occurred</li>
 *     <li>{@code status} - the HTTP status code</li>
 *     <li>{@code error} - the reason phrase of the HTTP status</li>
 *     <li>{@code message} - a detailed message about the error</li>
 *     <li>{@code path} - the URI path that triggered the error</li>
 * </ul>
 * </p>
 * <p>
 * Lombok annotations used:
 * <ul>
 *     <li>{@code @Data} - generates getters, setters, toString, equals, and hashCode</li>
 *     <li>{@code @Builder} - enables the builder pattern for object creation</li>
 *     <li>{@code @AllArgsConstructor} - generates a constructor with all fields</li>
 * </ul>
 */
@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {

    /**
     * The timestamp indicating when the error occurred.
     */
    private Instant timestamp;

    /**
     * The HTTP status code associated with the error.
     */
    private int status;

    /**
     * The HTTP reason phrase (e.g., "Not Found", "Internal Server Error").
     */
    private String error;

    /**
     * A human-readable error message providing more context.
     */
    private String message;

    /**
     * The path of the HTTP request that caused the error.
     */
    private String path;
}
