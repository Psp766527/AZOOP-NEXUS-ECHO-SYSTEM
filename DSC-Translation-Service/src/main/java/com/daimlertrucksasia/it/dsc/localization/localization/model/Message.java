package com.daimlertrucksasia.it.dsc.localization.localization.model;

import lombok.Builder;
import lombok.Data;

/**
 * Represents a message exchanged between services that may require localization.
 * <p>
 * This object contains all metadata and dynamic arguments necessary to resolve
 * a message for a specific locale, with support for substitution and tracking.
 * </p>
 *
 * <p>Typically used in request/response flows involving internationalized content.</p>
 *
 * @since 1.0
 */
@Data
@Builder
public class Message {

    /**
     * Unique identifier used to trace the request and its corresponding response.
     */
    private String requestId;

    /**
     * Name or identifier of the service initiating the message (sender).
     */
    private String sourceService;

    /**
     * Name or identifier of the target service that should process the message.
     */
    private String targetService;

    /**
     * Locale for which the message should be localized (e.g., "en", "ja").
     */
    private String locale;

    /**
     * Dynamic arguments to be injected into the message template,
     * replacing placeholders like {0}, {1}, etc.
     */
    private Object[] args;

    /**
     * Raw message template or content before localization is applied.
     */
    private String content;

    /**
     * Fully resolved and localized message after substitution of arguments.
     * Typically used in the response after processing.
     */
    private String resolvedMessage;

    /**
     * Timestamp representing when the message was created or sent.
     * Usually stored in epoch milliseconds (UTC).
     */
    private long timestamp;
}
