package com.azoopindia.it.asi.traffic.manager.filter;

import com.azoopindia.it.asi.traffic.manager.config.DynamicRoutingProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gateway filter for dynamically routing REST and GraphQL requests based on service discovery metadata.
 * <p>
 * This filter:
 * <ul>
 *   <li>Routes REST calls using a prefixed URI pattern (e.g., /api/serviceId/...).</li>
 *   <li>Routes GraphQL calls by matching GraphQL operation names to registered services via Eureka metadata.</li>
 *   <li>Refreshes service metadata every 60 seconds automatically.</li>
 * </ul>
 * <p>
 * GraphQL services must expose their supported operations via the Eureka metadata field:
 * <pre>
 *     eureka.instance.metadata-map:
 *         graphql.operations: createUser,updateUser,deleteUser
 * </pre>
 */
@Slf4j
@Component("DynamicUniversalRoutingFilter")
@RequiredArgsConstructor
@RefreshScope
public class DynamicUniversalRoutingFilter extends AbstractGatewayFilterFactory<Object> {

    /**
     * Load balancer URI prefix (default is "lb://").
     * Used to build request URI dynamically for routing.
     */
    @Value("${spring.loadBalancerURI:lb://}")
    private String loadBalancerURI;

    /**
     * Discovery client for fetching registered service instances (e.g., from Eureka).
     */
    private final ReactiveDiscoveryClient discovery;

    /**
     * Custom configuration properties for routing behavior.
     */
    private final DynamicRoutingProperties props;

    /**
     * Jackson ObjectMapper for parsing GraphQL request bodies.
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Cache of GraphQL operation name → service ID mappings.
     * This is populated based on Eureka metadata.
     */
    private final Map<String, String> gqlCache = new ConcurrentHashMap<>();

    /**
     * Set of currently active service IDs discovered from Eureka.
     */
    private final Set<String> activeServiceIds = ConcurrentHashMap.newKeySet();

    /**
     * Cached Mono that completes when the initial metadata load is done.
     * Ensures routing does not proceed before initialization is complete.
     */
    private Mono<Void> initComplete = Mono.defer(this::refreshOnce).cache();

    /**
     * Builds the filter with a specific order of execution.
     *
     * @param config unused
     * @return ordered Gateway filter
     */
    @Override
    public GatewayFilter apply(Object config) {
        return new OrderedGatewayFilter(this::filter, 10100);
    }

    /**
     * Initializes a periodic refresh of service metadata every 60 seconds.
     * Runs after Spring context initialization.
     */
    @PostConstruct
    void refreshLoop() {
        Mono.defer(this::refreshOnce)
                .thenMany(Flux.interval(Duration.ofSeconds(60)))
                .flatMap(tick -> refreshOnce())
                .subscribe();
    }

    /**
     * Refreshes the GraphQL operation mappings and service discovery metadata from Eureka.
     * Parses the `graphql.operations` metadata entry to update internal routing cache.
     *
     * @return a Mono that completes after refresh
     */
    private Mono<Void> refreshOnce() {
        return discovery.getServices()
                .flatMap(discovery::getInstances)
                .collectList()
                .flatMap(instances -> {
                    Set<String> discoveredServices = new LinkedHashSet<>();
                    gqlCache.clear();

                    for (ServiceInstance inst : instances) {
                        String serviceId = inst.getServiceId().toLowerCase();
                        discoveredServices.add(serviceId);

                        Map<String, String> metadata = inst.getMetadata();
                        String ops = metadata.get("graphql.operations");

                        if (StringUtils.hasText(ops)) {
                            Arrays.stream(ops.split(","))
                                    .map(String::trim)
                                    .filter(op -> !op.isEmpty())
                                    .forEach(op -> gqlCache.put(op, serviceId));
                            log.info("Service '{}' registered GraphQL ops via metadata: {}", serviceId, ops);
                        }
                    }

                    activeServiceIds.clear();
                    activeServiceIds.addAll(discoveredServices);

                    return Mono.empty();
                });
    }

    /**
     * Entry point for the Gateway filter logic.
     * Delegates routing to either GraphQL or REST based on request path and content type.
     *
     * @param exchange the current server exchange
     * @param chain    the Gateway filter chain
     * @return a Mono indicating filter completion
     */
    private Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        MediaType contentType = exchange.getRequest().getHeaders().getContentType();

        if (path.endsWith("/graphql") && MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            return routeGraphQL(exchange, chain);
        }

        if (path.startsWith(props.getGatewayPrefix())) {
            return routeRest(exchange, chain, path);
        }

        return chain.filter(exchange);
    }

    /**
     * Routes a GraphQL request to the appropriate service based on the operation name.
     * Extracts the GraphQL operation name from the request body and looks it up in the cache.
     *
     * @param exchange the current request/response context
     * @param chain    the Gateway filter chain
     * @return a Mono indicating filter completion
     */
    private Mono<Void> routeGraphQL(ServerWebExchange exchange, GatewayFilterChain chain) {
        return initComplete.then(
                DataBufferUtils.join(exchange.getRequest().getBody())
                        .flatMap(buffer -> {
                            byte[] rawBody = new byte[buffer.readableByteCount()];
                            buffer.read(rawBody);
                            DataBufferUtils.release(buffer);

                            String bodyStr = new String(rawBody, StandardCharsets.UTF_8);
                            String operationName = extractOperationName(bodyStr);

                            if (!StringUtils.hasText(operationName)) {
                                log.debug("No operation name found in GraphQL request.");
                                return chain.filter(exchange);
                            }

                            String serviceId = gqlCache.get(operationName);
                            if (!StringUtils.hasText(serviceId)) {
                                log.warn("Operation '{}' not mapped to any service yet.", operationName);
                                return refreshOnce().then(chain.filter(exchange));
                            }

                            URI uri = URI.create(loadBalancerURI + serviceId + props.getGraphql().getServicePath());
                            exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, uri);

                            ServerHttpRequestDecorator decoratedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                                @Override
                                public Flux<org.springframework.core.io.buffer.DataBuffer> getBody() {
                                    return Flux.just(exchange.getResponse().bufferFactory().wrap(rawBody));
                                }
                            };

                            return chain.filter(exchange.mutate().request(decoratedRequest).build());
                        }));
    }

    /**
     * Extracts the GraphQL operation name from a JSON request body.
     * Falls back to parsing the query text if `operationName` is not present.
     *
     * @param body the raw GraphQL JSON body
     * @return the operation name, or null if not found
     */
    private String extractOperationName(String body) {
        try {
            JsonNode node = mapper.readTree(body);
            if (node.hasNonNull("operationName") && !node.get("operationName").isNull()) {
                return node.get("operationName").asText();
            }

            String query = node.hasNonNull("query") ? node.get("query").asText() : null;
            if (query == null) return null;

            int startIdx = query.indexOf('{');
            if (startIdx < 0) return null;

            String afterBrace = query.substring(startIdx + 1).trim();
            String[] split = afterBrace.split("[\\s(]", 2);
            return split.length > 0 ? split[0] : null;

        } catch (Exception e) {
            log.warn("Failed to extract GraphQL operation name: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Routes a REST request based on URI prefix mapping (e.g., /api/serviceId/...).
     * Optionally strips the prefix from the outgoing request path.
     *
     * @param exchange the current exchange
     * @param chain    the Gateway filter chain
     * @param fullPath the full incoming request path
     * @return a Mono indicating filter completion
     */
    private Mono<Void> routeRest(ServerWebExchange exchange, GatewayFilterChain chain, String fullPath) {
        String internal = fullPath.substring(props.getGatewayPrefix().length());
        int slashIdx = internal.indexOf('/');
        if (slashIdx < 1) {
            log.warn("Malformed REST path: {}", fullPath);
            return chain.filter(exchange);
        }

        String serviceId = internal.substring(0, slashIdx).toLowerCase();
        String remainingPath = internal.substring(slashIdx);

        URI uri = props.getRest().getStripPrefix()
                ? URI.create(loadBalancerURI + serviceId + remainingPath)
                : URI.create(loadBalancerURI + serviceId + "/" + internal);

        exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, uri);
        log.info("Routing REST path '{}' → '{}'", fullPath, uri);
        return chain.filter(exchange);
    }
}

