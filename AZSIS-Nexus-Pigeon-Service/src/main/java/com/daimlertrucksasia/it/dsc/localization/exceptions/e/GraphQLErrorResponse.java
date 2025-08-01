package com.daimlertrucksasia.it.dsc.localization.exceptions.e;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A custom implementation of {@link GraphQLError} used to standardize
 * error responses in GraphQL APIs.
 *
 * <p>This class provides structured error information such as
 * status codes, error codes, timestamps, and user-defined details,
 * and it integrates seamlessly with Spring Boot + SPQR GraphQL.</p>
 *
 * <p>It supports enhanced diagnostics through the {@code extensions}
 * map returned by {@link #getExtensions()}, which can be consumed
 * by frontend clients or logs.</p>
 *
 * <p>Built using Lombok's {@code @Builder} and {@code @Data}
 * annotations to simplify boilerplate code.</p>
 *
 * <b>Example Usage:</b>
 * <pre>{@code
 * throw new GraphQLException(
 *     GraphQLErrorResponse.builder()
 *         .message("User not found")
 *         .errorCode("NOT_FOUND")
 *         .status(404)
 *         .timestamp(OffsetDateTime.now())
 *         .build()
 * );
 * }</pre>
 *
 * @since 1.0
 */
@Builder
@Data
public class GraphQLErrorResponse implements GraphQLError {

    /**
     * A short human-readable error message.
     */
    private String message;

    /**
     * The path within the GraphQL query where the error occurred.
     */
    private List<Object> path;

    /**
     * The location(s) in the query where the error occurred.
     */
    private List<SourceLocation> locations;

    /**
     * A custom application-specific error code.
     */
    private String errorCode;

    /**
     * Additional details about the error, if available.
     */
    private String details;

    /**
     * HTTP-style status code representing the error.
     */
    private int status;

    /**
     * The timestamp when the error was generated.
     */
    private OffsetDateTime timestamp;

    /**
     * Returns the error message.
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * Returns the locations in the GraphQL query related to this error.
     */
    @Override
    public List<SourceLocation> getLocations() {
        return locations != null ? locations : Collections.emptyList();
    }

    /**
     * Returns a GraphQL-specific error type based on the custom error code.
     */
    @Override
    public ErrorClassification getErrorType() {
        return switch (errorCode != null ? errorCode : "") {
            case "NOT_FOUND" -> graphql.ErrorType.DataFetchingException;
            case "VALIDATION_FAILED" -> graphql.ErrorType.ValidationError;
            default -> graphql.ErrorType.DataFetchingException;
        };
    }

    /**
     * Returns the GraphQL query path where the error occurred.
     */
    @Override
    public List<Object> getPath() {
        return path != null ? path : Collections.emptyList();
    }

    /**
     * Returns a map of additional error metadata for client interpretation.
     */
    @Override
    public Map<String, Object> getExtensions() {
        return Map.of(
                "code", errorCode != null ? errorCode : "UNKNOWN",
                "details", details != null ? details : "",
                "status", status,
                "timestamp", timestamp != null ? timestamp.toString() : OffsetDateTime.now().toString()
        );
    }
}
