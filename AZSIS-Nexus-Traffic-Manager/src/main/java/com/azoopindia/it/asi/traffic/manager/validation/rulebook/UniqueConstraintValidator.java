package com.azoopindia.it.asi.traffic.manager.validation.rulebook;

import com.azoopindia.it.asi.traffic.manager.exception.DuplicateRecordFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 * Validator component responsible for parsing {@link DataIntegrityViolationException}
 * instances and generating user-friendly error messages, particularly for unique constraint violations.
 * <p>
 * This class is commonly used to handle raw database error messages (e.g., from PostgreSQL or MySQL)
 * and convert them into human-readable formats that can be sent back to clients.
 * </p>
 *
 * <p><b>Example:</b><br>
 * Given a raw error like:<br>
 * {@code ERROR: duplicate key value violates unique constraint "user_email_key" Detail: Key (email)=(test@example.com) already exists.}<br>
 * This class will extract and return:<br>
 * {@code "Duplicate entry: email 'test@example.com' already exists."}
 * </p>
 *
 */
@Component
public class UniqueConstraintValidator {


    public String apply(Object entity) {

        throw new DuplicateRecordFoundException("Duplicate Record Found");

    }

    /**
     * Extracts a friendly error message from a {@link DataIntegrityViolationException},
     * specifically when a unique constraint (i.e., duplicate key) is violated.
     *
     * @param ex the {@link DataIntegrityViolationException} thrown by the database
     * @return a user-friendly error message describing the constraint violation,
     * or a generic message if the message pattern is not recognized
     */
    public String extractFriendlyMessage(DataIntegrityViolationException ex) {
        String rootMsg = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();

        if (rootMsg != null && rootMsg.contains("duplicate key")) {
            String field = extractFieldName(rootMsg);
            String value = extractFieldValue(rootMsg);
            return String.format("Duplicate entry: %s '%s' already exists.", field, value);
        }

        return "Data integrity violation.";
    }

    /**
     * Attempts to extract the name of the database field that violated the unique constraint
     * from the given raw database error message.
     * <p>
     * Assumes the format contains a substring like {@code Key (field_name)=}.
     * </p>
     *
     * @param message the raw error message from the database
     * @return the extracted field name, or "unknown_field" if parsing fails
     */
    private String extractFieldName(String message) {
        int start = message.indexOf("Key (");
        int end = message.indexOf(")=", start);
        if (start != -1 && end != -1) {
            return message.substring(start + 5, end);
        }
        return "unknown_field";
    }

    /**
     * Attempts to extract the duplicate value from the raw database error message.
     * <p>
     * Assumes the format contains a substring like {@code =(value)}.
     * </p>
     *
     * @param message the raw error message from the database
     * @return the extracted value that caused the constraint violation, or "unknown_value" if parsing fails
     */
    private String extractFieldValue(String message) {
        int start = message.indexOf(")=(");
        int end = message.indexOf(")", start + 3);
        if (start != -1 && end != -1) {
            return message.substring(start + 3, end);
        }
        return "unknown_value";
    }
}
