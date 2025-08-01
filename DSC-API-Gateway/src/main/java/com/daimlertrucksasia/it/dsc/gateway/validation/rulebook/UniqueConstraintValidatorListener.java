package com.daimlertrucksasia.it.dsc.gateway.validation.rulebook;

import com.daimlertrucksasia.it.dsc.gateway.rate.limiting.config.entity.RateLimitConfig;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueConstraintValidatorListener {

    private UniqueConstraintValidator uniqueConstraintValidator;

    @Autowired
    public void init(UniqueConstraintValidator injectedValidator) {
        uniqueConstraintValidator = injectedValidator;
    }

    @PrePersist
    @PreUpdate
    public void validate(RateLimitConfig config) {
        uniqueConstraintValidator.apply(config);
    }
}
