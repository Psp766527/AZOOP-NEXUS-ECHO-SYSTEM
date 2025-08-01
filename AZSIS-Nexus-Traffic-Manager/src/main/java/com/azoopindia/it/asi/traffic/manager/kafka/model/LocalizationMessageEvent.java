package com.azoopindia.it.asi.traffic.manager.kafka.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * Represents the transport-layer event used for localization message exchange
 * between services (e.g., via Kafka).
 *
 * <p>
 * This class is part of the messaging system and is typically serialized and
 * deserialized when sending or receiving messages across service boundaries.
 * It carries all metadata required for processing a localization request,
 * such as correlation IDs, message templates, and locale information.
 * </p>
 *
 * <p>
 * <strong>Note:</strong> Although this class shares several fields with
 * { MessageEventAuditEntity},
 * they serve different purposes:
 * </p>
 *
 * <p>
 * {@code LocalizationMessageEvent} is a concrete, non-sealed subclass of {@link EventType}
 * used for representing localization-related messages in the system.
 *
 * <p>This event structure is primarily utilized in asynchronous communication
 * between microservices, especially for message localization workflows.
 * It encapsulates metadata and content required to request and respond
 * with localized messages based on user and service contexts.</p>
 *
 * <p>Typical use cases include:</p>
 * <ul>
 *     <li>Sending a message localization request from one microservice to another.</li>
 *     <li>Tracking message processing with correlation IDs.</li>
 *     <li>Publishing and consuming structured messages via Kafka topics.</li>
 *     <li>Storing or auditing message events for debugging and traceability.</li>
 * </ul>
 *
 * <p>Each event carries unique identifiers and contextual metadata to ensure
 * correct routing, processing, and logging of message localization activities.</p>
 *
 * <p>This class is designed to be serialized and transferred over messaging systems
 * such as Kafka. It follows immutability and consistency guidelines by using Lombok
 * annotations {@code @Data} and {@code @EqualsAndHashCode}.</p>
 *
 * @see EventType
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public non-sealed class LocalizationMessageEvent extends EventType {

    /**
     * Unique identifier used to trace the request and its corresponding response.
     * Typically, generated once per client request to ensure correlation
     * across distributed services.
     */
    private String requestId;

    /**
     * Unique user correlation ID used to trace the correct user and
     * associate responses back to the originator in a multi-tenant setup.
     */
    private String userCoRelationID;

    /**
     * Unique service correlation ID used to trace the correct service
     * and ensure routing between sender and receiver microservices.
     */
    private String serviceCoRelationID;

    /**
     * Identifier for the message template to be localized.
     * This ID groups related messages across different locales.
     * For example, it may represent a key in a localization catalog.
     */
    private String msgTemplateID;

    /**
     * Name or identifier of the source service initiating the localization request.
     * Helps track message origin and implement authorization or auditing.
     */
    private String sourceService;

    /**
     * Name or identifier of the target service responsible for processing
     * and returning the localized message.
     */
    private String targetService;

    /**
     * Locale code (e.g., "en", "ja", "de") indicating the target language
     * for message localization.
     */
    private String locale;

    /**
     * A list of dynamic arguments that should be injected into the message template.
     * These values replace placeholders like {0}, {1} in the raw content.
     */
    private List<String> args;

    /**
     * The raw message content or template before localization or formatting is applied.
     * It may contain placeholders for dynamic injection.
     */
    private String content;

    /**
     * The fully localized and resolved message string after the target service
     * has processed the request and performed argument substitution.
     */
    private String resolvedMessage;

    /**
     * Timestamp representing when this message was created or sent.
     * The value should be recorded in epoch milliseconds (UTC).
     * Useful for ordering, auditing, and debugging purposes.
     */
    private String msgResolutionTimestamp;

    /**
     * Timestamp representing when this message was created or sent.
     * The value should be recorded in epoch milliseconds (UTC).
     * Useful for ordering, auditing, and debugging purposes.
     */
    private String msgCreationTimestamp;
}
