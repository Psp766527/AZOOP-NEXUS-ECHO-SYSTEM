package com.daimlertrucksasia.it.dsc.gateway.exception;

/**
 * Exception thrown when an input field that is no longer supported or deprecated is provided.
 *
 * <p>This can be used to signal clients that a particular field in their request payload
 * is obsolete and should be removed. It helps enforce newer API standards or discourage
 * usage of legacy configurations.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * if (request.getRequestsPerMinute() != null) {
 *     throw new InputFieldNoLongerSupported("The 'requestsPerMinute' field is deprecated and no longer supported.");
 * }
 * }</pre>
 *
 * @since 1.0
 */
public class InputFieldNoLongerSupported extends RuntimeException {

    /**
     * Constructs a new {@code InputFieldNoLongerSupported} exception with a specific message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public InputFieldNoLongerSupported(String message) {
        super(message);
    }
}
