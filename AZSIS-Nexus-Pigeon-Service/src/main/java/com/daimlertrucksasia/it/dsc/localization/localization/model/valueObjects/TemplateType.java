package com.daimlertrucksasia.it.dsc.localization.localization.model.valueObjects;

/**
 * Represents the different types of templates that can be used for localized messages
 * in the DSC Pigeon localization service.
 * <p>
 * These template types categorize the context or medium in which the message is used,
 * such as UI alerts, system messages, or communication formats like emails or texts.
 * </p>
 */
public enum TemplateType {

    /**
     * Represents an error message template, typically shown to users when something fails unexpectedly.
     */
    ERROR,

    /**
     * Represents a success message template, used to confirm successful operations.
     */
    SUCCESS,

    /**
     * Represents a failure message template, typically used to communicate expected or handled failures.
     */
    FAILURE,

    /**
     * Represents a message template meant to be used in an email format.
     */
    EMAIL,

    /**
     * Represents a message template formatted for short text communication (e.g., SMS or in-app message).
     */
    TEXT,

    /**
     * Represents a message template displayed as a popup within a UI.
     */
    POPUP,

    /**
     * Represents a special type of alert message that appears in popup format, usually for critical or important notifications.
     */
    ALERT_POPUP
}
