
package com.azoopindia.it.asi.traffic.manager.kafka.service.producer;

import com.azoopindia.it.asi.traffic.manager.kafka.config.KafkaPropertiesConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * Configuration class that defines a {@link RetryTemplate} bean
 * customized for Kafka producer retry handling.
 *
 * <p>This bean configures the retry behavior including the back-off period
 * between retries and the maximum number of retry attempts. These properties
 * are injected from the application's property source (e.g., application.properties
 * or application.yml) with keys:
 * <ul>
 *   <li>{@code spring.localization.kafka.producer.retry.BackOffPeriod}</li>
 *   <li>{@code spring.localization.kafka.producer.retry.MaxRetryAttempts}</li>
 * </ul>
 * </p>
 *
 * <p>The retry template uses a fixed back-off policy and a simple retry policy.</p>
 *
 * <p>The configured {@link RetryTemplate} can be injected and used in Kafka producer
 * components to automatically retry message sending on failure with the configured settings.</p>
 *
 */

@Configuration
public class ProducerRetryBean {


    /**
     * Back-off period in milliseconds to wait between retry attempts.
     */

    @Value("${spring.apigateway.kafka.producer.retry.BackOffPeriod}")
    private long backOffPeriod;

    /**
     * Maximum number of retry attempts (including the first attempt).
     */

    @Value("${spring.apigateway.kafka.producer.retry.MaxRetryAttempts}")
    private int maxRetryAttempts;

    private final KafkaPropertiesConfig kafkaPropertiesConfig;

    public ProducerRetryBean(KafkaPropertiesConfig kafkaPropertiesConfig) {
        this.kafkaPropertiesConfig = kafkaPropertiesConfig;
    }


    /**
     * Creates and configures a {@link RetryTemplate} bean with fixed back-off
     * and simple retry policy.
     *
     * @return a configured {@code RetryTemplate} instance to be used for retrying Kafka producer operations
     */

    @Bean
    @Qualifier("kafkaProducerRetryTemplate")
    public RetryTemplate kafkaProducerRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(backOffPeriod);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(maxRetryAttempts);

        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }
}

