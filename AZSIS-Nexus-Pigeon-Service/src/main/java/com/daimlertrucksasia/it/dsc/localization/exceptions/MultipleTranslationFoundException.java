package com.daimlertrucksasia.it.dsc.localization.exceptions;

/**
 * Exception thrown when multiple translations are found for the same message template
 * and locale combination, where only one was expected.
 *
 * <p>This exception is typically used to indicate a data integrity issue in the
 * localized message storage (e.g., MongoDB), such as a violation of uniqueness
 * constraints on {@code msgTemplateID} and {@code locale}.</p>
 *
 * <p>It extends {@link RuntimeException}, so it is an unchecked exception and does
 * not need to be declared or caught explicitly.</p>
 *
 * <b>Example Usage:</b>
 * <pre>{@code
 * if (translations.size() > 1) {
 *     throw new MultipleTranslationFoundException("Multiple translations found for template ID: " + msgTemplateID);
 * }
 * }</pre>
 *
 * @since 1.0
 */
public class MultipleTranslationFoundException extends RuntimeException {

    /**
     * Constructs a new {@code MultipleTranslationFoundException} with the specified detail message.
     *
     * @param msg the detail message describing the exception
     */
    public MultipleTranslationFoundException(String msg) {
        super(msg);
    }
}
