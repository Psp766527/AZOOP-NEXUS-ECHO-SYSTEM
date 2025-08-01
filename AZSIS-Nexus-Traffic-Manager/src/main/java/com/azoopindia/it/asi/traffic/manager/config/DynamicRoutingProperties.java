package com.azoopindia.it.asi.traffic.manager.config;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration class that binds properties prefixed with {@code dynamic-routing}
 * from the application configuration (e.g., application.yml or application.properties).
 *
 * <p>This configuration is used to define dynamic routing behavior for both REST and GraphQL
 * endpoints in a Spring Cloud Gateway setup.
 *
 * <p>Structure:
 * <ul>
 *   <li>{@code gatewayPrefix}: Common REST path prefix used for identifying routes.</li>
 *   <li>{@code graphql}: Nested GraphQL-specific routing configuration.</li>
 *   <li>{@code rest}: Nested REST-specific routing configuration.</li>
 * </ul>
 *
 * <p>Example configuration in application.yml:
 * <pre>
 * dynamic-routing:
 *   gatewayPrefix: /api/
 *   graphql:
 *     inboundPath: /dsc/api/graphql/v1
 *     servicePath: /internal/graphql
 *   rest:
 *     stripPrefix: true
 * </pre>
 *
 * Daimler Trucks Asia Digital Services Team
 */

@Data
@Validated
@ConfigurationProperties(prefix = "dynamic-routing")
public class DynamicRoutingProperties {

    /**
     * The common REST path prefix used for identifying REST service routes.
     * Example: {@code /api/}
     */
    @NotBlank
    private String gatewayPrefix;

    @NotNull
    private Graphql graphql;

    @NotNull
    private Rest rest;

    /**
     * Configuration properties related to GraphQL routing.
     * Used for matching and routing incoming GraphQL requests.
     */
    @Data
    public static class Graphql {
        /**
         * The path pattern for inbound GraphQL requests.
         * Typically, matches client requests.
         * Example: {@code /dsc/api/graphql/v1}
         */

        @NotBlank
        private String inboundPath;

        /**
         * The internal path to which GraphQL requests should be forwarded in downstream services.
         * Example: {@code /internal/graphql}
         */

        @NotBlank
        private String servicePath;
    }

    /**
     * Configuration properties related to REST routing behavior.
     */
    @Data
    public static class Rest {
        /**
         * Indicates whether to strip the service prefix from the URL path before routing
         * to downstream REST services.
         *
         * <p>If true, the prefix is removed. Otherwise, the entire path is forwarded.
         */
        @NotNull
        private Boolean stripPrefix;
    }
}

