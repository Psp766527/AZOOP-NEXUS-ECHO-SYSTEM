package com.daimlertrucksasia.it.dsc.gateway.kafka.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Configuration properties holder for Kafka-related settings under the prefix
 * {@code spring.localization.kafka}.
 *
 * <p>This class is annotated with {@link ConfigurationProperties} and
 * {@link Configuration} to enable automatic binding of properties defined
 * in application configuration files (e.g., application.yml or application.properties)
 * into this bean.</p>
 *
 * <p>The properties include:</p>
 * <ul>
 *   <li>{@code bootstrapServers} - Kafka broker connection string(s).</li>
 *   <li>{@code producer} - A map of Kafka producer-specific configuration properties.</li>
 *   <li>{@code consumer} - A map of Kafka consumer-specific configuration properties.</li>
 * </ul>
 *
 * <p>The {@code producer} and {@code consumer} maps allow for flexible and
 * extensible configuration of Kafka client properties without having to
 * explicitly define each property in this class.</p>
 *
 * <p>Example properties in {@code application-dev.yml} that bind to this class:</p>
 * <pre>{@code
 * spring:
 *   localization:
 *     kafka:
 *       bootstrapServers: localhost:9092
 *       producer:
 *         retries: 3
 *         batchSize: 16384
 *       consumer:
 *         groupId: my-group
 *         autoOffsetReset: earliest
 * }</pre>
 *
 * <p>This configuration bean can be injected wherever Kafka configuration
 * properties are needed.</p>
 *
 */
@Configuration
@ConfigurationProperties(prefix = "spring.apigateway.kafka")
@Getter
@Setter
public class KafkaPropertiesConfig {

    /**
     * Kafka bootstrap servers connection string(s).
     * Typically, a comma-separated list of host:port pairs.
     */
    private String bootstrapServers;

    /**
     * Map holding configuration properties specific to the Kafka producer client.
     * The keys and values correspond to Kafka producer configuration options.
     */
    private Map<String, Object> producer;

    /**
     * Map holding configuration properties specific to the Kafka consumer client.
     * The keys and values correspond to Kafka consumer configuration options.
     */
    private Map<String, Object> consumer;
}
