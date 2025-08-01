package com.azoopindia.it.asi.traffic.manager.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service responsible for managing routing logic of GraphQL operations
 * to their respective service identifiers in a distributed environment.
 *
 * <p>This service maintains an in-memory registry that maps GraphQL operation
 * names to service IDs.It allows registering new operations dynamically
 * and resolving the target service for any given operation.</p>
 *
 * <p>Thread safety is ensured through the use of {@link ConcurrentHashMap} for storing the mappings.</p>
 *
 * <p>This class is marked as a Spring {@code @Service} and is intended to be used
 * as a singleton within the application context.</p>
 */
@Service
public class GraphQLOperationRoutingService {

    /**
     * Internal concurrent map maintaining operation-to-service mappings.
     * The key is the GraphQL operation name, and the value is the associated service ID.
     */
    private final Map<String, String> operationToService = new ConcurrentHashMap<>();

    /**
     * Registers one or more GraphQL operations to the specified service.
     *
     * @param serviceId  the identifier of the service registering the operations
     * @param operations the list of GraphQL operation names to associate with the service
     */
    public void register(String serviceId, List<String> operations) {
        for (String op : operations) {
            operationToService.put(op, serviceId);
        }
    }

    /**
     * Resolves the service ID responsible for handling the given GraphQL operation.
     *
     * @param operationName the name of the GraphQL operation to resolve
     * @return an {@link Optional} containing the service ID if found, or empty if not registered
     */
    public Optional<String> resolveService(String operationName) {
        return Optional.ofNullable(operationToService.get(operationName));
    }

    /**
     * Retrieves an immutable snapshot of all current GraphQL operation-to-service mappings.
     *
     * @return a read-only {@link Map} of all registered operation mappings
     */
    public Map<String, String> getAllMappings() {
        return Map.copyOf(operationToService);
    }
}
