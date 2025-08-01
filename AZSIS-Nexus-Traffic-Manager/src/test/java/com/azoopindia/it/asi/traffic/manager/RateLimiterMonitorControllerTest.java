package com.azoopindia.it.asi.traffic.manager;

import com.azoopindia.it.asi.traffic.manager.application.controller.RateLimiterMonitorController;
import com.azoopindia.it.asi.traffic.manager.service.RateLimiterService;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link RateLimiterMonitorController}.
 * <p>
 * This class validates the behavior of the monitoring endpoint for
 * rate limiter tokens and capacities, based on mocked service responses.
 */
@ExtendWith(MockitoExtension.class)
class RateLimiterMonitorControllerTest {

    @Mock
    private RateLimiterService rateLimiterService;

    @InjectMocks
    private RateLimiterMonitorController controller;

    private ConcurrentHashMap<String, Bucket> bucketMap;
    private ConcurrentHashMap<String, Long> configMap;

    /**
     * Sets up mocked bucket and configuration cache data.
     * <p>
     * A mock Bucket is created and preconfigured with available tokens,
     * associated with a sample clientId-route key.
     * <p>
     * The stubbing uses {@code lenient()} to avoid UnnecessaryStubbingException in tests that don’t use the mock.
     */
    @BeforeEach
    void setUp() {
        Bucket mockBucket = mock(Bucket.class);
        lenient().when(mockBucket.getAvailableTokens()).thenReturn(42L);

        bucketMap = new ConcurrentHashMap<>();
        configMap = new ConcurrentHashMap<>();

        bucketMap.put("client1:/api/test", mockBucket);
        configMap.put("client1:/api/test", 100L);
    }

    /**
     * Verifies that the controller returns correct available tokens and capacity
     * for existing buckets and their configuration values.
     */
    @Test
    void shouldReturnAvailableTokensAndCapacity() {
        when(rateLimiterService.getBucketCache()).thenReturn(bucketMap);
        when(rateLimiterService.getConfigCache()).thenReturn(configMap);

        Map<String, Object> result = controller.getAllLimits();

        assertThat(result).containsKey("client1:/api/test");

        @SuppressWarnings("unchecked")
        Map<String, Object> stats = (Map<String, Object>) result.get("client1:/api/test");

        assertThat(stats.get("availableTokens")).isEqualTo(42L);
        assertThat(stats.get("capacity")).isEqualTo(100L);

        verify(rateLimiterService).getBucketCache();
        verify(rateLimiterService).getConfigCache();
    }

    /**
     * Verifies that if a bucket is present but no corresponding config value is found,
     * the controller returns -1 for capacity.
     */
    @Test
    void shouldReturnMinusOneCapacityIfMissingInConfig() {
        configMap.clear(); // Simulate config cache miss

        when(rateLimiterService.getBucketCache()).thenReturn(bucketMap);
        when(rateLimiterService.getConfigCache()).thenReturn(configMap);

        Map<String, Object> result = controller.getAllLimits();

        assertThat(result).containsKey("client1:/api/test");

        @SuppressWarnings("unchecked")
        Map<String, Object> stats = (Map<String, Object>) result.get("client1:/api/test");

        assertThat(stats.get("availableTokens")).isEqualTo(42L);
        assertThat(stats.get("capacity")).isEqualTo(-1L);
    }

    /**
     * Verifies that when no buckets or config entries exist,
     * the controller returns an empty map.
     */
    @Test
    void shouldReturnEmptyMapWhenNoBucketsPresent() {
        when(rateLimiterService.getBucketCache()).thenReturn(new ConcurrentHashMap<>());
        when(rateLimiterService.getConfigCache()).thenReturn(new ConcurrentHashMap<>());

        Map<String, Object> result = controller.getAllLimits();

        assertThat(result).isEmpty();
    }
}
