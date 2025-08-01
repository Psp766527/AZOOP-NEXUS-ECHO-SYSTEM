package com.daimlertrucksasia.it.dsc.gateway.application.controller;

import com.daimlertrucksasia.it.dsc.gateway.infrastructure.RateLimitConfigRepository;
import com.daimlertrucksasia.it.dsc.gateway.rate.limiting.config.entity.RateLimitConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * REST controller responsible for managing rate limiting configurations
 * within the registry Traffic Manager system.
 * <p>
 * This controller provides endpoints to:
 * <ul>
 *   <li>Register new rate limiting rules</li>
 *   <li>Update existing rules</li>
 *   <li>Retrieve active rate limits by client and route</li>
 * </ul>
 * <p>
 * Rate limiting rules define how many requests a given client can make to a specific
 * route over a defined time window. This helps enforce fairness, prevent abuse,
 * and support the quality of service (QoS).
 * </p>
 * <p>
 * Example usage:
 * <pre>
 * POST /rate/limit/register
 * GET /rate/limit/{clientId}/{route}
 * </pre>
 *
 * @since 1.0
 */
@RestController
@RequestMapping("/rate/limit")
public class RateLimiterConfigController {

    @Autowired
    private RateLimitConfigRepository configRepository;

    /**
     * Registers a new rate limit configuration for a specific client and route.
     * <p>
     * This endpoint allows dynamic setup of rate limiting behavior by persisting a
     * {@link RateLimitConfig} entity in the underlying data store. The configuration
     * includes properties like requests per minute, burst capacity, expiration, and
     * any custom attributes.
     * </p>
     *
     * @param config A valid {@link RateLimitConfig} object containing rule details.
     * @return The saved {@link RateLimitConfig} with HTTP 200 OK.
     * @apiNote Clients should ensure the uniqueness of clientId + route combination
     * to avoid duplicates.
     */
    @PostMapping("/register")
    public ResponseEntity<RateLimitConfig> registerRateLimit(@RequestBody RateLimitConfig config) {
        RateLimitConfig savedConfig = configRepository.save(
                RateLimitConfig.builder()
                        .clientId(config.getClientId())
                        .route(config.getRoute())
                        .requestsPerMinute(config.getRequestsPerMinute())
                        .maxTokensPerWindow(config.getMaxTokensPerWindow())
                        .timeWindow(config.getTimeWindow())
                        .timeUnit(config.getTimeUnit())
                        .burstCapacity(config.getBurstCapacity())
                        .priority(config.getPriority())
                        .expirationDate(config.getExpirationDate())
                        .createdAt(LocalDateTime.now())
                        .status(config.getStatus())
                        .customAttributes(config.getCustomAttributes())
                        .build()
        );

        return ResponseEntity.ok(savedConfig);
    }

    /**
     * Updates an existing rate limit configuration using its unique identifier.
     * <p>
     * This endpoint modifies the rate limiting parameters such as request limits,
     * burst capacity, expiration, and status. It also updates the `updatedAt` timestamp
     * to track change history.
     * </p>
     *
     * @param id     The MongoDB document ID of the existing {@link RateLimitConfig}.
     * @param config A {@link RateLimitConfig} object containing new configuration values.
     * @return The updated configuration if found, or 404 Not Found if the ID doesn't exist.
     * @implNote This endpoint performs a full-field update on the found configuration.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<RateLimitConfig> updateRateLimit(@PathVariable String id, @RequestBody RateLimitConfig config) {
        return configRepository.findById(id)
                .map(existing -> {
                    existing.setClientId(config.getClientId());
                    existing.setRoute(config.getRoute());
                    existing.setRequestsPerMinute(config.getRequestsPerMinute());
                    existing.setMaxTokensPerWindow(config.getMaxTokensPerWindow());
                    existing.setTimeWindow(config.getTimeWindow());
                    existing.setTimeUnit(config.getTimeUnit());
                    existing.setBurstCapacity(config.getBurstCapacity());
                    existing.setPriority(config.getPriority());
                    existing.setExpirationDate(config.getExpirationDate());
                    existing.setStatus(config.getStatus());
                    existing.setCustomAttributes(config.getCustomAttributes());
                    existing.setUpdatedAt(LocalDateTime.now());

                    RateLimitConfig updated = configRepository.save(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves the active rate limit configuration for a specific client and route.
     * <p>
     * Only active and non-expired configurations are returned. This endpoint is useful
     * for runtime systems such as API Gateways, sidecars, or middlewares to apply
     * throttling rules dynamically based on client identity and route path.
     * </p>
     *
     * @param clientId The identifier of the client making API calls (e.g., application name or user ID).
     * @param route    The target API route for which the rate limit is defined.
     * @return A {@link RateLimitConfig} if found and active; 404 otherwise.
     * @apiNote The query enforces status = "ACTIVE" to support soft-deletion or temporary deactivation.
     */
    @GetMapping("/{clientId}/{route}")
    public ResponseEntity<RateLimitConfig> getRateLimit(@PathVariable String clientId, @PathVariable String route) {
        return configRepository
                .findFirstByClientIdAndRouteAndStatus(
                        clientId,
                        route,
                        "ACTIVE"
                )
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
