package com.daimlertrucksasia.it.dsc.localization.exceptions;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * Represents a standardized error response structure to be returned from the API
 * in case of exceptions or validation failures.
 *
 * <p>This class is useful for clients to consistently parse and handle error messages.
 * It typically gets serialized to JSON when returned from REST controllers.</p>
 *
 * <p>It includes fields such as the timestamp of the error, HTTP status code,
 * application-specific error code, error message, and the request path that caused the error.</p>
 *
 * <p>Annotated with {@link lombok.Data} to generate boilerplate code (getters, setters, toString, etc.),
 * and {@link lombok.Builder} to support fluent creation using the builder pattern.</p>
 *
 * <b>Example JSON Output:</b>
 * <pre>{@code
 * {
 *   "timestamp": "2025-06-23T12:34:56Z",
 *   "status": 404,
 *   "errorCode": "MSG_TEMPLATE_NOT_FOUND",
 *   "message": "MSG template not found with the messageTemplateID.",
 *   "path": "/msg/template/job.card.link.error.JobCard"
 * }
 * }</pre>
 *
 * @since 1.0
 */
@Data
@Builder
public class ErrorResponse {

    /**
     * The timestamp indicating when the error occurred.
     */
    private OffsetDateTime timestamp;

    /**
     * The HTTP status code (e.g., 400, 404, 500).
     */
    private int status;

    /**
     * The application-specific error code for client-side handling.
     */
    private String errorCode;

    /**
     * A human-readable message describing the error.
     */
    private String message;

    /**
     * The URI path of the HTTP request that caused the error.
     */
    private String path;
}
