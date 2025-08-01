package com.azoopindia.it.asi.traffic.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link DSCGatewayApplicationTests}.
 * <p>
 * These tests validate that the Spring Boot application context loads correctly
 * and expected beans are available.
 */
@SpringBootTest
class DSCGatewayApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Test to ensure that the Spring application context loads without issues.
     */
    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }

    /**
     * Test to ensure that a key service or filter bean is loaded.
     * You can extend this to check for custom filters/services.
     */
    @Test
    void shouldLoadCustomBeans() {
        boolean hasRateLimiterService = applicationContext.containsBeanDefinition("rateLimiterService");
        boolean hasSomeGatewayFilter = applicationContext.containsBeanDefinition("RateLimiterFilter");

        assertThat(hasRateLimiterService).isTrue(); // If your service is named 'rateLimiterService'
        assertThat(hasSomeGatewayFilter).isTrue(); // Assuming at least one custom filter is loaded
    }

    /**
     * Smoke test to ensure main() executes without exceptions.
     * This doesn't start a real web environment.
     */
    @Test
    void mainMethodRunsWithoutError() {
        DSCGatewayApplication.main(new String[]{});
    }
}

