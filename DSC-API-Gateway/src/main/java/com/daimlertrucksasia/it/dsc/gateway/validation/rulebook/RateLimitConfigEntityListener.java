package com.daimlertrucksasia.it.dsc.gateway.validation.rulebook;

import com.daimlertrucksasia.it.dsc.gateway.rate.limiting.config.entity.RateLimitConfig;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * JPA EntityListener for the {@link RateLimitConfig} entity.
 * <p>
 * This listener hooks into the JPA lifecycle events using {@code @PrePersist} and {@code @PreUpdate}
 * to perform validation of deprecated fields before an entity is inserted or updated in the database.
 * </p>
 *
 * <p>
 * It delegates the actual validation logic to the {@link ValidateDeprecatedFields} Spring-managed bean,
 * enabling the use of Spring features such as dependency injection inside JPA callbacks.
 * </p>
 *
 * <p>
 * This listener should be registered on the {@link RateLimitConfig} entity using the {@code @EntityListeners} annotation.
 * </p>
 * <p>
 * Example:
 * <pre>{@code
 * @EntityListeners(RateLimitConfigEntityListener.class)
 * public class RateLimitConfig {
 *     ...
 * }
 * }</pre>
 *
 * @see ValidateDeprecatedFields
 * @see RateLimitConfig
 */
@Component
public class RateLimitConfigEntityListener {

    /**
     * Static reference to the Spring-managed validator bean.
     * Initialized via setter injection to work with JPA lifecycle.
     */
    private static ValidateDeprecatedFields validator;

    /**
     * Initializes the static validator reference.
     * This method is automatically called by Spring to inject the {@link ValidateDeprecatedFields} bean.
     *
     * @param injectedValidator the Spring bean used for deprecated field validation
     */
    @Autowired
    public void init(ValidateDeprecatedFields injectedValidator) {
        validator = injectedValidator;
    }

    /**
     * JPA lifecycle callback executed before persisting or updating a {@link RateLimitConfig} entity.
     * Delegates to {@link ValidateDeprecatedFields#validate(Object)} to check for usage of deprecated fields.
     *
     * @param config the {@link RateLimitConfig} entity being persisted or updated
     */
    @PrePersist
    @PreUpdate
    public void validate(RateLimitConfig config) {
        validator.validate(config);
    }
}
