
package com.daimlertrucksasia.it.dsc.gateway.kafka.service.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Service class responsible for sending messages to Kafka topics related to the Pigeon system.
 * <p>
 * This class uses a {@link KafkaTemplate} for sending messages and a {@link RetryTemplate} to
 * automatically retry message sending on failures.
 * </p>
 * <p>
 * The KafkaTemplate and RetryTemplate beans are injected using Spring's dependency injection,
 * distinguished by their respective qualifiers.
 * </p>
 * <p>
 * Logging is enabled to trace message send attempts and failures.
 * </p>
 *
 */

@Slf4j
@Service
public class KafkaProducerService {

    @Qualifier("pigeonKafkaTemplate")
    private final KafkaTemplate<String, String> pigeonKafkaTemplate;

    @Qualifier("kafkaProducerRetryTemplate")
    private final RetryTemplate kafkaProducerRetryTemplate;

    /**
     * Constructs a {@code KafkaProducerService} with injected KafkaTemplate and RetryTemplate.
     *
     * @param pigeonKafkaTemplate        the Kafka template used to send messages to Kafka topics
     * @param kafkaProducerRetryTemplate the retry template used to retry sending messages on failure
     */

    public KafkaProducerService(KafkaTemplate<String, String> pigeonKafkaTemplate, RetryTemplate kafkaProducerRetryTemplate) {
        this.pigeonKafkaTemplate = pigeonKafkaTemplate;
        this.kafkaProducerRetryTemplate = kafkaProducerRetryTemplate;
    }


    /**
     * Sends a message to the specified Kafka topic with retry logic.
     * <p>
     * The method attempts to send the message using the configured {@link KafkaTemplate}. If the
     * send operation fails, it is retried according to the configured {@link RetryTemplate}.
     * </p>
     * <p>
     * Each attempt is logged, and if all retry attempts fail, an error is logged.
     * </p>
     *
     * @param pigeonTopic the name of the Kafka topic to send the message to
     * @param key         the key for the message
     * @param msgContent  the content of the message to send
     */

    public void sendMsg(String pigeonTopic, String key, String msgContent) {
        log.info("Sending message to topic: {}, key: {}, message: {}", pigeonTopic, key, msgContent);

        try {
            kafkaProducerRetryTemplate.execute(context -> {
                pigeonKafkaTemplate.send(pigeonTopic, key, msgContent).get(); // blocks until send completes
                log.info("Message sent on attempt #{}", context.getRetryCount() + 1);
                return null;
            }, context -> {
                log.error("All retry attempts failed for message to topic: {}", pigeonTopic);
                return null;
            });
        } catch (Exception ex) {
            log.error("error in sending message to {}", pigeonTopic);
        }
    }
}

