package com.azoopindia.it.asi.traffic.manager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing a request to register GraphQL operations
 * associated with a specific service in the traffic manager system.
 *
 * <p>This request enables dynamic registration of GraphQL operation names
 * which the system can later route to the appropriate service.</p>
 *
 * <p>The registration request includes:
 * <ul>
 *     <li>{@code serviceId} – the unique identifier of the service registering the operations</li>
 *     <li>{@code operations} – a list of GraphQL operation names to be registered under the specified service</li>
 * </ul>
 * </p>
 *
 * <p>Example JSON payload:
 * <pre>{@code
 * {
 *   "serviceId": "user-profile-service",
 *   "operations": [
 *     "getUserDetails",
 *     "updateUserProfile",
 *     "deleteUserAccount"
 *   ]
 * }
 * }</pre>
 * </p>
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraphQLOperationRegistrationRequest {

    /**
     * Unique identifier for the service registering GraphQL operations.
     */
    private String serviceId;

    /**
     * List of GraphQL operation names to register for the given service.
     */
    private List<String> operations;
}
