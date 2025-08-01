package com.daimlertrucksasia.it.dsc.localization.exceptions;

/**
 * Exception thrown when no localized message or translation is found
 * for a specific locale and message template.
 *
 * <p>This exception typically indicates that the requested localization
 * data is missing in the backend storage, such as a MongoDB collection.</p>
 *
 * <p>It extends {@link RuntimeException}, making it an unchecked exception
 * that can be thrown without requiring explicit handling.</p>
 *
 * <b>Example Usage:</b>
 * <pre>{@code
 * if (localizedMessage == null) {
 *     throw new NoConversionFoundForLocale("No translation available for locale: " + locale);
 * }
 * }</pre>
 *
 * @since 1.0
 */
public class NoConversionFoundForLocale extends RuntimeException {

    /**
     * Constructs a new {@code NoConversionFoundForLocale} with the specified detail message.
     *
     * @param msg the detail message describing the missing conversion or translation
     */
    public NoConversionFoundForLocale(String msg) {
        super(msg);
    }
}
