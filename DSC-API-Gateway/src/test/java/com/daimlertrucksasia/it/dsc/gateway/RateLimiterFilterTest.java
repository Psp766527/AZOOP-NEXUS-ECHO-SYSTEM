package com.daimlertrucksasia.it.dsc.gateway;

import com.daimlertrucksasia.it.dsc.gateway.filter.RateLimiterFilter;
import com.daimlertrucksasia.it.dsc.gateway.rate.limiting.config.entity.RateLimitConfig;
import com.daimlertrucksasia.it.dsc.gateway.service.RateLimiterService;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link RateLimiterFilter} using JUnit 5 and Mockito.
 * These tests cover rate limiting behavior, retries, configuration parsing, and exception fallback.
 */
@ExtendWith(MockitoExtension.class)
class RateLimiterFilterTest {

    @Mock
    private RateLimiterService rateLimiterService;

    @Mock
    private GatewayFilterChain mockChain;

    @Mock
    private Bucket mockBucket;

    @InjectMocks
    private RateLimiterFilter rateLimiterFilter;

    /**
     * Builds a {@link ServerWebExchange} with the given path and client ID header.
     */
    private ServerWebExchange buildExchange(String path, String clientId) {
        return MockServerWebExchange.from(
                MockServerHttpRequest.get(path)
                        .header("X-Client-Id", clientId)
                        .build()
        );
    }

    /**
     * Test that verifies 400 Bad Request is returned when the `X-Client-Id` header is missing.
     */
    @Test
    void shouldReturnBadRequestIfClientIdIsMissing() {
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/test").build());

        Mono<Void> result = rateLimiterFilter.apply(new RateLimiterFilter.Config())
                .filter(exchange, mockChain);

        result.block(); // block instead of StepVerifier

        assertThat(exchange.getResponse().getStatusCode().value()).isEqualTo(400);
        verifyNoInteractions(rateLimiterService);
    }

    /**
     * Test that verifies the filter allows request to proceed when the bucket allows token consumption.
     */
    @Test
    void shouldAllowRequestIfBucketAllowsConsumption() {
        String clientId = "client-1";
        String path = "/api/test";

        when(rateLimiterService.getConfig(clientId, path)).thenReturn(null);
        when(rateLimiterService.resolveBucket(clientId, path)).thenReturn(mockBucket);
        when(mockBucket.tryConsume(1)).thenReturn(true);
        when(mockChain.filter(any())).thenReturn(Mono.empty());

        ServerWebExchange exchange = buildExchange(path, clientId);

        Mono<Void> result = rateLimiterFilter.apply(new RateLimiterFilter.Config())
                .filter(exchange, mockChain);

        result.block(); // blocking for test purposes

        verify(mockChain).filter(exchange);
    }

    /**
     * Test that verifies the request is rejected with 429 status when bucket does not allow consumption and retries are not configured.
     */
    @Test
    void shouldRejectRequestWhenRateLimitExceededWithoutRetry() {
        String clientId = "client-2";
        String path = "/api/limited";

        RateLimitConfig config = RateLimitConfig.builder().customAttributes(new HashMap<>()).build();

        when(rateLimiterService.getConfig(clientId, path)).thenReturn(config);
        when(rateLimiterService.resolveBucket(clientId, path)).thenReturn(mockBucket);
        when(mockBucket.tryConsume(1)).thenReturn(false);

        ServerWebExchange exchange = buildExchange(path, clientId);

        Mono<Void> result = rateLimiterFilter.apply(new RateLimiterFilter.Config())
                .filter(exchange, mockChain);

        result.block();

        assertThat(exchange.getResponse().getStatusCode().value()).isEqualTo(429);
    }

    /**
     * Test that verifies a request is retried once (based on configuration) and eventually allowed through.
     */
    @Test
    void shouldRetryAndSucceedIfConfigured() {
        String clientId = "client-3";
        String path = "/api/retry";

        Map<String, String> attrs = new HashMap<>();
        attrs.put("maxRetries", "1");
        attrs.put("retryDelay", "10");

        RateLimitConfig config = RateLimitConfig.builder().customAttributes(attrs).build();

        when(rateLimiterService.getConfig(clientId, path)).thenReturn(config);
        when(rateLimiterService.resolveBucket(clientId, path)).thenReturn(mockBucket);
        when(mockBucket.tryConsume(1)).thenReturn(false, true); // First call fails, second succeeds
        when(mockChain.filter(any())).thenReturn(Mono.empty());

        ServerWebExchange exchange = buildExchange(path, clientId);

        Mono<Void> result = rateLimiterFilter.apply(new RateLimiterFilter.Config())
                .filter(exchange, mockChain);

        result.block();

        verify(mockBucket, times(2)).tryConsume(1);
        verify(mockChain).filter(exchange);
    }

    /**
     * Test that ensures parsing failures in custom attributes (e.g., non-numeric retry settings) do not crash the system.
     * Default values are used instead.
     */
    @Test
    void shouldFallbackToDefaultsOnParsingErrorInCustomAttributes() {
        String clientId = "client-ex";
        String path = "/api/parse-error";

        Map<String, String> attrs = new HashMap<>();
        attrs.put("maxRetries", "invalid");  // Causes NumberFormatException
        attrs.put("retryDelay", "also-bad");

        RateLimitConfig config = RateLimitConfig.builder().customAttributes(attrs).build();

        when(rateLimiterService.getConfig(clientId, path)).thenReturn(config);
        when(rateLimiterService.resolveBucket(clientId, path)).thenReturn(mockBucket);
        when(mockBucket.tryConsume(1)).thenReturn(true);
        when(mockChain.filter(any())).thenReturn(Mono.empty());

        ServerWebExchange exchange = buildExchange(path, clientId);

        Mono<Void> result = rateLimiterFilter.apply(new RateLimiterFilter.Config())
                .filter(exchange, mockChain);

        result.block(); // Trigger execution

        verify(mockChain).filter(exchange); // Still allowed through
    }
}
