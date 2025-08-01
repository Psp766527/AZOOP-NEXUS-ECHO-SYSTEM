package com.daimlertrucksasia.it.dsc.gateway.kafka.helper;

import java.util.UUID;

/**
 * Utility class responsible for generating globally unique request IDs
 * for event correlation across distributed microservices.
 *
 * <p>Each ID generated is guaranteed to be unique by leveraging a combination of:
 * <ul>
 *     <li>Time-based entropy (System nanotime or timestamp)</li>
 *     <li>Universally Unique Identifier (UUID)</li>
 * </ul>
 *
 * <p>These request IDs are critical for tracking message flow and routing the
 * response from M2 service to the correct thread awaiting it. The request ID acts
 * as a correlation token, enabling the mapping of asynchronous responses back
 * to their originators.</p>
 *
 * <p>Usage Example:
 *  <pre>{@code
 *     String requestId = UniqueEventRequestIdGenerator.generateRequestId();
 *  }</pre>
 *
 * <p>Note: This class is thread-safe.</p>
 */
public class UniqueEventRequestIdGenerator {

    /**
     * Generates a globally unique request ID string. If in case of EventType is not identified,
     * then this default requestID generation will make sure that each request and corresponding response will be mapped
     * to the correct client/user/thread who is waiting for the requested response
     *
     * <p>The ID is composed of:
     * <ul>
     *     <li>A UUID (Universally Unique Identifier)</li>
     *     <li>An optional prefix (if needed for classification)</li>
     * </ul>
     * The result is a 128-bit random-based string, virtually guaranteed to never repeat.
     * </p>
     *
     * @return a unique request ID string, e.g., {@code "REQ-12fd4e02-aefc-437b-a5d6-b3c8329a918e"}
     */
    public static String generateRequestId() {
        return "REQ-" + UUID.randomUUID();
    }

    /**
     * Generates a unique request ID string for localization msg.
     *
     * <p>The ID is composed of:
     * <ul>
     *     <li>A UUID (Universally Unique Identifier)</li>
     *     <li>An optional prefix (if needed for classification)</li>
     * </ul>
     * The result is a 128-bit random-based string, virtually guaranteed to never repeat.
     * </p>
     *
     * @return a unique request ID string, e.g., {@code "REQ-MSG-LOCALE-12fd4e02-aefc-437b-a5d6-b3c8329a918e"}
     */
    public static String generateRequestIdForLocalization() {
        return "REQ-MSG-LOCALE-" + UUID.randomUUID();
    }
}
