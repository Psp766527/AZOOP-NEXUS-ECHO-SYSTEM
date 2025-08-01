package com.azoopindia.it.asi.traffic.manager.kafka.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka configuration class responsible for setting up the Kafka consumer factory
 * and listener container factory. This configuration enables concurrent Kafka message
 * consumption using Spring Kafka dynamically which depends on spring active profile like in resources folder.
 *
 * <p>
 * Components configured:
 * - ConsumerFactory: Provides configuration for Kafka consumers.
 * - ConcurrentKafkaListenerContainerFactory: Enables concurrent message listeners.</p>
 *
 * <p>
 * This class should be annotated with @Configuration and @EnableKafka to ensure proper
 * Kafka support is enabled in the Spring application context.</p>
 */

@Slf4j
@Configuration
@EnableKafka
@RequiredArgsConstructor
public class Kafka_P_And_C_Config {

    private final KafkaPropertiesConfig kafkaProperties;

    /**
     * Creates a custom {@link ProducerFactory} bean for Kafka message production.
     *
     * <p>The factory uses StringSerializer for both key and value serialization and
     * reads Kafka broker address from the configuration property {@code kafka.bootstrap.servers}.</p>
     *
     * @return a {@link DefaultKafkaProducerFactory} configured for String key-value pairs
     */
    @Bean
    @Qualifier("kafkaPigeonProducerFactory")
    public ProducerFactory<String, String> kafkaPigeonProducerFactory() {
        Map<String, Object> kafkaProducerFactoryConfig = new HashMap<>();
        try {
            kafkaProducerFactoryConfig = new HashMap<>(kafkaProperties.getProducer());

            // Merge nested "properties" (e.g., security.protocol, sasl configs)
            @SuppressWarnings("unchecked") Map<String, Object> extraProps = (Map<String, Object>) kafkaProperties.getProducer().get("properties");
            if (extraProps != null) {
                kafkaProducerFactoryConfig.putAll(extraProps);
            }

            kafkaProducerFactoryConfig.forEach((key, value) -> log.debug("Producer Key {} and Producer Value {}", key, value));

            kafkaProducerFactoryConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
            kafkaProducerFactoryConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Class.forName((String) kafkaProperties.getProducer().get("key-serializer")));
            kafkaProducerFactoryConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, Class.forName((String) kafkaProperties.getProducer().get("value-serializer")));

            //log.debug("Kafka Producer Config: {}", kafkaProducerFactoryConfig);

            return new DefaultKafkaProducerFactory<>(kafkaProducerFactoryConfig);

        } catch (Exception ex) {
            log.error("Error In Producer Factory Configuration While Starting the Kafka Producer Factory Config. ", ex.fillInStackTrace());
            return new DefaultKafkaProducerFactory<>(kafkaProducerFactoryConfig);
        }
    }

    /**
     * Defines a {@link KafkaTemplate} bean for sending messages to Kafka topics.
     *
     * <p>This template uses the custom producer factory defined in {@code kafkaPigeonProducerFactory()}
     * and is qualified with {@code kafkaTemplate} so that it can be injected where needed.</p>
     *
     * @return a {@link KafkaTemplate} for publishing String key-value messages
     */
    @Bean
    @Qualifier("pigeonKafkaTemplate")
    public KafkaTemplate<String, String> pigeonKafkaTemplate() {
        return new KafkaTemplate<>(kafkaPigeonProducerFactory());
    }

    /**
     * Creates and configures a {@link ConsumerFactory} for String key-value pairs.
     *
     * <p>This factory is responsible for creating Kafka Consumer instances with the
     * desired deserializers and properties.</p>
     *
     * @return a configured {@link ConsumerFactory} for String keys and values
     */
    @Bean
    @Qualifier("kafkaPigeonConsumerFactory")
    public ConsumerFactory<String, String> kafkaPigeonConsumerFactory() {

        Map<String, Object> kafkaConsumerFactoryConfig = new HashMap<>();

        try {
            kafkaConsumerFactoryConfig = new HashMap<>(kafkaProperties.getConsumer());

            // Merge nested "properties" (e.g., security.protocol, sasl configs)
            @SuppressWarnings("unchecked") Map<String, Object> extraProps = (Map<String, Object>) kafkaProperties.getConsumer().get("properties");
            if (extraProps != null) {
                kafkaConsumerFactoryConfig.putAll(extraProps);
            }

            kafkaConsumerFactoryConfig.forEach((key, value) -> log.debug("Consumer Key {} and Consumer Value {}", key, value));

            kafkaConsumerFactoryConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
            kafkaConsumerFactoryConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Class.forName((String) kafkaProperties.getConsumer().get("key-deserializer")));
            kafkaConsumerFactoryConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, Class.forName((String) kafkaProperties.getConsumer().get("value-deserializer")));

            log.debug("Kafka Consumer Config: {}", kafkaConsumerFactoryConfig);
        } catch (Exception ex) {
            log.error("Error In Consumer Factory Configuration While Starting the Kafka Consumer Factory Config. ", ex.fillInStackTrace());
        }
        return new DefaultKafkaConsumerFactory<>(kafkaConsumerFactoryConfig);
    }

    /**
     * Kafka Listener Container Factory Bean configuration.
     * <p>
     * This factory is used by Spring Kafka to create listener containers for consuming messages.
     * It supports batch consumption and concurrent message processing.
     *
     * @param consumerFactory the Kafka ConsumerFactory used to create Kafka consumers
     * @return a configured ConcurrentKafkaListenerContainerFactory
     */
    @Bean
    @Qualifier("kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(5);
        return factory;
    }

    /**
     * Configures a {@link ConcurrentKafkaListenerContainerFactory} for concurrent Kafka listeners.
     *
     * <p>This factory enables support for concurrent Kafka message consumption using the
     *
     * @return a configured {@link ConcurrentKafkaListenerContainerFactory} bean
     * @KafkaListener annotation. It uses the {@code consumerFactory()} bean to create consumers.</p>
     */
    //@Bean
    //@Qualifier("kafkaListenerContainerFactory")
    /*public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaPigeonConsumerFactory());
        return factory;
    }*/
}
