package com.daimlertrucksasia.it.dsc.localization.helper.graphql.schema.operation.registration;

import com.netflix.appinfo.EurekaInstanceConfig;
import graphql.GraphQL;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLSchema;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Registers the GraphQL operation names (queries and mutations) of the current service instance
 * as metadata in the Eureka registry.
 * <p>
 * This metadata is later used by gateway filters (such as {@code DynamicUniversalRoutingFilter})
 * to dynamically route GraphQL requests based on the operation name.
 * <p>
 * Registered under the metadata key: <b>{@code graphql.operations}</b>.
 * <p>
 * Example value in Eureka:
 * <pre>
 * graphql.operations=createUser,updateUser,getUsers
 * </pre>
 * <p>
 * This registration happens once on service startup.
 */
@Component
public class EurekaGraphQLOperationRegistrar {

    /**
     * The GraphQL runtime instance injected from Spring.
     * Used to access the service's GraphQL schema and operation definitions.
     */
    @Autowired
    private GraphQL graphQL;

    /**
     * Eureka instance configuration used to dynamically inject metadata.
     */
    @Autowired
    private EurekaInstanceConfig eurekaInstanceConfig;

    /**
     * Lifecycle hook that runs after the bean is initialized.
     * <p>
     * Extracts all top-level query and mutation operation names from the GraphQL schema
     * and registers them in Eureka metadata under the key {@code graphql.operations}.
     */
    @PostConstruct
    public void registerOperationsAsMetadata() {
        GraphQLSchema schema = graphQL.getGraphQLSchema();

        // Collect all query operation names
        Set<String> queries = schema.getQueryType().getFieldDefinitions().stream()
                .map(GraphQLFieldDefinition::getName)
                .collect(Collectors.toSet());

        // Collect all mutation operation names, if any
        Set<String> mutations = schema.getMutationType() != null
                ? schema.getMutationType().getFieldDefinitions().stream()
                .map(GraphQLFieldDefinition::getName)
                .collect(Collectors.toSet())
                : Set.of();

        // Combine both queries and mutations
        Set<String> allOps = new java.util.HashSet<>(queries);
        allOps.addAll(mutations);

        // Join into comma-separated string for Eureka metadata
        String opsString = String.join(",", allOps);

        // Register the operations into Eureka metadata map
        eurekaInstanceConfig.getMetadataMap().put("graphql.operations", opsString);
    }
}
