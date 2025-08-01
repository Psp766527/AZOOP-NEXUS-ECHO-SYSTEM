package com.daimlertrucksasia.it.dsc.localization.localization.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing a request to create or update
 * a localized message in the system.
 * <p>
 * This model is used to capture client input for localized message data,
 * including the message template, locale, message content, and identifiers
 * for the service provider and consumer.
 * </p>
 *
 * <p>All fields are required and validated using {@link NotNull} constraints.</p>
 */
@Data
public class LocalizedMsgRequest {

    /**
     * Unique identifier for the message template.
     * This ID is used to group localized messages across different languages.
     */
    @JsonProperty("messageTemplateID")
    @NotNull
    private String msgTemplateID;

    /**
     * Locale code for the message (e.g., "en_US", "ja_JP").
     * Determines the language and region formatting of the message.
     */
    @JsonProperty("locale")
    @NotNull
    private String locale;

    /**
     * The actual localized message content to be stored or retrieved.
     */
    @JsonProperty("message")
    @NotNull
    private String message;

    /**
     * The placeholder/arguments which need to placed dynamically in the localized msg.
     */
    @JsonProperty("placeholders")
    private List<String> placeholders;

    /**
     * Identifier for the service or system that is providing the message.
     */
    @JsonProperty("serviceProviderID")
    @NotNull
    private String serviceProviderID;

    /**
     * Identifier for the service or system that will consume the message.
     */
    @JsonProperty("serviceConsumerID")
    @NotNull
    private String serviceConsumerID;
}
