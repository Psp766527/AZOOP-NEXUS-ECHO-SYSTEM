package com.azoopindia.it.asi.traffic.manager.validation.rulebook;

import com.azoopindia.it.asi.traffic.manager.exception.InputFieldNoLongerSupported;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * Validator component to enforce restrictions on deprecated fields within JPA entities.
 *
 * <p>This class performs reflection-based inspection of entity fields annotated with {@link Deprecated}.
 * If any deprecated fields are found to contain meaningful values, a {@link InputFieldNoLongerSupported}
 * exception is thrown, effectively blocking the persistence or update operation.</p>
 *
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>Can be injected and reused as a Spring {@link Component} across the application.</li>
 *   <li>Intended to be used in conjunction with an {@code EntityListener} (e.g., {@code @PrePersist},
 *   {@code @PreUpdate}).</li>
 *   <li>Supports detection of non-zero numbers, non-empty strings, and non-null values for deprecated fields.</li>
 * </ul>
 *
 * <h3>Usage:</h3>
 * <p>Use this validator in an entity listener such as {@code RateLimitConfigEntityListener}
 * to ensure deprecated fields like {@code requestsPerMinute} are not used during persistence.</p>
 * <p>
 * Example:
 * <pre>{@code
 * @PrePersist
 * @PreUpdate
 * public void validate(RateLimitConfig config) {
 *     validator.validate(config);
 * }
 * }</pre>
 *
 * <p>This helps enforce clean migration to new fields while providing user-friendly error reporting.</p>
 *
 * @see InputFieldNoLongerSupported
 * @see jakarta.persistence.PrePersist
 * @see jakarta.persistence.PreUpdate
 */
@Slf4j
@Component
public class ValidateDeprecatedFields {

    /**
     * Validates that no deprecated fields are used in the given entity object.
     *
     * <p>If any fields annotated with {@link Deprecated} contain non-default values (e.g. non-zero, non-null),
     * this method throws an {@link InputFieldNoLongerSupported} exception with a list of offending field names.</p>
     *
     * @param entity The JPA entity object being validated.
     * @throws InputFieldNoLongerSupported if any deprecated fields are found to be in use.
     */
    public void validate(Object entity) {
        StringBuilder deprecatedUsed = new StringBuilder();

        for (Field field : entity.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Deprecated.class)) {
                field.setAccessible(true);
                try {
                    Object value = field.get(entity);

                    boolean isUsed = (value instanceof Number && ((Number) value).longValue() > 0)
                            || (value instanceof String && !((String) value).isBlank())
                            || (value != null && !(value instanceof Number || value instanceof String));

                    if (isUsed) {
                        deprecatedUsed.append(field.getName()).append(", ");
                    }
                } catch (IllegalAccessException ex) {
                    log.error("Reflection error while validating deprecated fields: {}", ex.getMessage(), ex);
                }
            }
        }

        if (!deprecatedUsed.isEmpty()) {
            String usedFields = deprecatedUsed.substring(0, deprecatedUsed.length() - 2);
            throw new InputFieldNoLongerSupported(
                    String.format("The following deprecated fields are no longer supported: %s ", usedFields)
            );
        }
    }
}
