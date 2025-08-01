package com.daimlertrucksasia.it.dsc.localization.exceptions;

/**
 * Exception thrown when a specific translation is not found for a given
 * message template ID and locale.
 *
 * <p>This exception typically signals that no localized message was found
 * in the backend data store (e.g., MongoDB) that matches the requested
 * message template and locale.</p>
 *
 * <p>It extends {@link RuntimeException}, making it an unchecked exception
 * suitable for use in service layers or data access logic without the need
 * for explicit catch blocks.</p>
 *
 * <b>Example Usage:</b>
 * <pre>{@code
 * LocalizedMessageEntity message = repository.findMessageByCodeAndLocale(msgId, locale);
 * if (message == null) {
 *     throw new TranslationNotFoundException("Translation not found for template ID: " + msgId + ", locale: " + locale);
 * }
 * }</pre>
 *
 * @since 1.0
 */
public class TranslationNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code TranslationNotFoundException} with the specified detail message.
     *
     * @param msg the detail message describing the missing translation
     */
    public TranslationNotFoundException(String msg) {
        super(msg);
    }
}
