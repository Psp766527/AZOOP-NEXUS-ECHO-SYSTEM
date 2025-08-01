package com.azoopindia.it.asi.traffic.manager;

import com.azoopindia.it.asi.traffic.manager.application.controller.RateLimiterConfigController;
import com.azoopindia.it.asi.traffic.manager.infrastructure.RateLimitConfigRepository;
import com.azoopindia.it.asi.traffic.manager.rate.limiting.config.entity.RateLimitConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit test class for {@link RateLimiterConfigController}.
 * Uses Mockito to mock dependencies and verify interactions with {@link RateLimitConfigRepository}.
 * Covers positive and negative test cases for each REST endpoint.
 */
@ExtendWith(MockitoExtension.class)
class RateLimiterConfigControllerTest {

    /**
     * Mocked repository to avoid actual database interaction.
     */
    @Mock
    private RateLimitConfigRepository configRepository;

    /**
     * Controller under test with mocked dependencies injected.
     */
    @InjectMocks
    private RateLimiterConfigController controller;

    /**
     * Sample configuration object used across tests.
     */
    private RateLimitConfig sampleConfig;

    /**
     * Initializes a reusable {@link RateLimitConfig} before each test.
     * This simulates a valid configuration object to be returned or persisted.
     */
    @BeforeEach
    void setUp() {
        sampleConfig = RateLimitConfig.builder()
                .clientId("client-1")
                .route("/api/test")
                .requestsPerMinute(100)
                .timeWindow(1)
                .timeUnit("MINUTES")
                .burstCapacity(200)
                .priority(1)
                .expirationDate(LocalDateTime.now().plusDays(1))
                .createdAt(LocalDateTime.now())
                .status("ACTIVE")
                .customAttributes(Map.of("retry", "3"))
                .build();
    }

    /**
     * Verifies that the controller correctly saves a new rate limit configuration.
     * Mocks the repository to return the same object after saving.
     */
    @Test
    void shouldRegisterNewRateLimitConfig() {
        when(configRepository.save(any())).thenReturn(sampleConfig);

        ResponseEntity<RateLimitConfig> response = controller.registerRateLimit(sampleConfig);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(sampleConfig);
        verify(configRepository).save(any(RateLimitConfig.class));
    }

    /**
     * Verifies that the controller updates an existing configuration when the ID is found.
     * Checks that updated values are persisted and returned properly.
     */
    @Test
    void shouldUpdateRateLimitConfigIfExists() {
        String id = "abc123";

        RateLimitConfig updatedConfig = RateLimitConfig.builder()
                .requestsPerMinute(150)
                .build();

        when(configRepository.findById(id)).thenReturn(Optional.of(sampleConfig));
        when(configRepository.save(any())).thenReturn(updatedConfig);

        ResponseEntity<RateLimitConfig> response = controller.updateRateLimit(id, updatedConfig);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getRequestsPerMinute()).isEqualTo(150);
        verify(configRepository).findById(id);
        verify(configRepository).save(any());
    }

    /**
     * Verifies that a 404 Not Found response is returned when attempting to update
     * a configuration that does not exist in the repository.
     */
    @Test
    void shouldReturnNotFoundWhenUpdatingNonexistentConfig() {
        String id = "not-found-id";

        when(configRepository.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<RateLimitConfig> response = controller.updateRateLimit(id, sampleConfig);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        verify(configRepository).findById(id);
        verify(configRepository, never()).save(any());
    }

    /**
     * Verifies that an existing rate limit configuration is returned correctly
     * when querying by client ID and route with status ACTIVE.
     */
    @Test
    void shouldReturnRateLimitConfigWhenFound() {
        String clientId = "client-1";
        String route = "/api/test";

        when(configRepository.findFirstByClientIdAndRouteAndStatus(clientId, route, "ACTIVE"))
                .thenReturn(Optional.of(sampleConfig));

        ResponseEntity<RateLimitConfig> response = controller.getRateLimit(clientId, route);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(sampleConfig);
        verify(configRepository).findFirstByClientIdAndRouteAndStatus(clientId, route, "ACTIVE");
    }

    /**
     * Verifies that a 404 Not Found response is returned when no active rate limit
     * configuration is found for the given client and route.
     */
    @Test
    void shouldReturnNotFoundIfRateLimitConfigNotFound() {
        String clientId = "client-x";
        String route = "/api/missing";

        when(configRepository.findFirstByClientIdAndRouteAndStatus(clientId, route, "ACTIVE"))
                .thenReturn(Optional.empty());

        ResponseEntity<RateLimitConfig> response = controller.getRateLimit(clientId, route);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        verify(configRepository).findFirstByClientIdAndRouteAndStatus(clientId, route, "ACTIVE");
    }
}
