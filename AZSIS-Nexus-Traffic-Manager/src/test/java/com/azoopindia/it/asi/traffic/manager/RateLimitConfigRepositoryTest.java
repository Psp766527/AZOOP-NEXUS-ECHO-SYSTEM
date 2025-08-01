package com.azoopindia.it.asi.traffic.manager;

import com.azoopindia.it.asi.traffic.manager.infrastructure.RateLimitConfigRepository;
import com.azoopindia.it.asi.traffic.manager.rate.limiting.config.entity.RateLimitConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Integration tests for {@link RateLimitConfigRepository}.
 * <p>
 * Ensures the correct behavior of query methods involving client ID, route, and status.
 */
@DataJpaTest
public class RateLimitConfigRepositoryTest {

    @Autowired
    private RateLimitConfigRepository configRepository;

    /**
     * Cleans up test data after each test method.
     */
    @AfterEach
    void tearDown() {
        configRepository.deleteAll();
    }

    /**
     * Tests that {@link RateLimitConfigRepository#findFirstByClientIdAndRouteAndStatus}
     * correctly returns a matching {@link RateLimitConfig} when clientId, route, and status match.
     */
    @Disabled("Disabled temporarily due to entity listener NPE")
    @DisplayName("Should return correct RateLimitConfig for matching clientId, route, and status")
    @Test
    void testFindFirstByClientIdAndRouteAndStatus_shouldReturnCorrectConfig() {
        // Given
        RateLimitConfig rateLimitConfig = RateLimitConfig.builder()
                .id(UUID.randomUUID())
                .clientId("client-001")
                .route("/localization/graphql")
                .requestsPerMinute(100)
                .timeWindow(300)
                .timeUnit("SECONDS")
                .burstCapacity(20)
                .priority(1)
                .expirationDate(LocalDateTime.of(2025, 12, 31, 23, 59))
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .customAttributes(new HashMap<>())
                .build();

        configRepository.save(rateLimitConfig);

        // When
        Optional<RateLimitConfig> result = configRepository.findFirstByClientIdAndRouteAndStatus(
                "client-001", "/localization/graphql", "ACTIVE");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getClientId()).isEqualTo("client-001");
        assertThat(result.get().getRoute()).isEqualTo("/localization/graphql");
        assertThat(result.get().getStatus()).isEqualTo("ACTIVE");
    }

    /**
     * Tests that {@link RateLimitConfigRepository#findFirstByClientIdAndRouteAndStatus}
     * returns an empty {@link Optional} when the status does not match.
     */
    @Disabled("Disabled temporarily due to entity listener NPE")
    @Test
    void testFindFirstByClientIdAndRouteAndStatus_shouldNotReturnIfStatusMismatch() {
/*

        RateLimitConfigEntityListener listener = new RateLimitConfigEntityListener();
        listener.setValidator(new ValidateDeprecatedFields());
*/

        // Given
        RateLimitConfig config = RateLimitConfig.builder()
                .clientId("client-002")
                .route("/localization/graphql")
                .status("INACTIVE")
                .build();
        configRepository.save(config);

        // When
        Optional<RateLimitConfig> result = configRepository.findFirstByClientIdAndRouteAndStatus(
                "client-002", "/localization/graphql", "ACTIVE");

        // Then
        assertThat(result).isEmpty();
    }

    /**
     * Tests that {@link RateLimitConfigRepository#findFirstByClientIdAndRouteAndStatus}
     * returns an empty {@link Optional} when no matching entry is found.
     */
    @Test
    void testFindFirstByClientIdAndRouteAndStatus_shouldReturnEmptyWhenNotFound() {
        // When
        Optional<RateLimitConfig> result = configRepository.findFirstByClientIdAndRouteAndStatus(
                "unknown-client", "/invalid", "INACTIVE");

        // Then
        assertThat(result).isEmpty();
    }
}
