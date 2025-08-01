package com.daimlertrucksasia.it.dsc.gateway;

import com.daimlertrucksasia.it.dsc.gateway.infrastructure.RateLimitConfigRepository;
import com.daimlertrucksasia.it.dsc.gateway.rate.limiting.config.entity.RateLimitConfig;
import com.daimlertrucksasia.it.dsc.gateway.service.RateLimiterService;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link RateLimiterService}.
 * <p>
 * Verifies caching behavior, default config fallback, repository access, and cache invalidation.
 */
@ExtendWith(MockitoExtension.class)
public class RateLimiterServiceTest {

    @Mock
    private RateLimitConfigRepository mockConfigRepository;

    @InjectMocks
    private RateLimiterService rateLimiterService;

    private RateLimitConfig config;

    /**
     * Clears repository after each test to prevent state leakage.
     */
    @AfterEach
    void tearDown() {
        mockConfigRepository.deleteAll();
    }

    /**
     * Initializes a reusable {@link RateLimitConfig} before each test.
     */
    @BeforeEach
    void setUp() {
        config = RateLimitConfig.builder()
                .id(UUID.randomUUID())
                .clientId("client-1")
                .route("/api/test")
                .requestsPerMinute(50)
                .burstCapacity(10)
                .timeWindow(1)
                .timeUnit("MINUTES")
                .priority(1)
                .expirationDate(LocalDateTime.of(2030, 1, 1, 0, 0))
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Verifies that a bucket is created and cached correctly when a valid rate limit config is found.
     */
    @Test
    void resolveBucket_shouldCreateAndCacheBucket_whenConfigExists() {
        // Given
        when(mockConfigRepository.findFirstByClientIdAndRouteAndStatus("client-1", "/api/test", "ACTIVE"))
                .thenReturn(Optional.of(config));

        // When
        Bucket bucket = rateLimiterService.resolveBucket("client-1", "/api/test");

        // Then
        assertThat(bucket).isNotNull();
        assertThat(rateLimiterService.getBucketCache()).containsKey("client-1:/api/test");
        assertThat(rateLimiterService.getConfigCache().get("client-1:/api/test")).isEqualTo(60L);
    }

    /**
     * Verifies that a default bucket is created when no matching rate limit config is found.
     * Also asserts that no value is stored in the config cache.
     */
    @Test
    void resolveBucket_shouldUseDefaultConfig_whenConfigNotFound() {
        // Given
        when(mockConfigRepository.findFirstByClientIdAndRouteAndStatus("unknown", "/no-route", "ACTIVE"))
                .thenReturn(Optional.empty());

        // When
        Bucket bucket = rateLimiterService.resolveBucket("unknown", "/no-route");

        // Then
        assertThat(bucket).isNotNull();
        assertThat(rateLimiterService.getBucketCache()).containsKey("unknown:/no-route");
        assertThat(rateLimiterService.getConfigCache().get("unknown:/no-route")).isNull();
    }

    /**
     * Tests that {@link RateLimiterService#getConfig(String, String)} returns the expected config from the repository.
     */
    @Test
    void getConfig_shouldReturnConfigIfExists() {
        // Given
        when(mockConfigRepository.findFirstByClientIdAndRouteAndStatus("client-1", "/api/test", "ACTIVE"))
                .thenReturn(Optional.of(config));

        // When
        RateLimitConfig result = rateLimiterService.getConfig("client-1", "/api/test");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getClientId()).isEqualTo("client-1");
    }

    /**
     * Tests that calling {@link RateLimiterService#invalidateCache(String, String)} removes entries from both internal caches.
     */
    @Test
    void invalidateCache_shouldRemoveFromBothCaches() {
        // Given
        rateLimiterService.getBucketCache().put("client-1:/api/test", mock(Bucket.class));
        rateLimiterService.getConfigCache().put("client-1:/api/test", 42L);

        // When
        rateLimiterService.invalidateCache("client-1", "/api/test");

        // Then
        assertThat(rateLimiterService.getBucketCache()).doesNotContainKey("client-1:/api/test");
        assertThat(rateLimiterService.getConfigCache()).doesNotContainKey("client-1:/api/test");
    }
}
