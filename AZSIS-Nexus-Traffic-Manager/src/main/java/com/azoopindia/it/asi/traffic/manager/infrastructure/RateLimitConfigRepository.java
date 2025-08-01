package com.azoopindia.it.asi.traffic.manager.infrastructure;

import com.azoopindia.it.asi.traffic.manager.rate.limiting.config.entity.RateLimitConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link RateLimitConfig} entities in MongoDB.
 * <p>
 * This interface extends {@link JpaRepository} to provide basic CRUD operations and
 * defines a custom method for retrieving active rate limit configurations by client and route.
 */
@Repository
public interface RateLimitConfigRepository extends JpaRepository<RateLimitConfig, String> {

    /**
     * Finds the first {@link RateLimitConfig} entry by client ID, route, and status.
     *
     * @param clientId the unique identifier of the client
     * @param route    the specific route for which the rate limit applies
     * @param status   the status of the rate limit config (e.g., "ACTIVE", "INACTIVE")
     * @return an {@link Optional} containing the matching {@link RateLimitConfig}, if found
     */
    Optional<RateLimitConfig> findFirstByClientIdAndRouteAndStatus(
            String clientId, String route, String status);
}
