
package com.daimlertrucksasia.it.dsc.gateway.kafka.service.consumer;


import com.daimlertrucksasia.it.dsc.gateway.kafka.model.LocalizationMessageEvent;
import com.daimlertrucksasia.it.dsc.gateway.kafka.service.producer.KafkaProducerService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * Kafka consumer service for listening to messages published to Kafka topics by the Pigeon system.
 * <p>
 * This class listens on configured topics using {@link KafkaListener} and logs received messages.
 * Additional message handling logic can be added in the consumer method.
 * </p>
 *
 * <p>
 * This service should be enabled in your application via Spring Boot configuration.
 * </p>
 * <p>
 * Example:
 * <pre>
 *     @KafkaListener(topics = "topic", groupId = "group")
 *     public void consume(String message) {
 *         // handle message
 *     }
 * </pre>
 *
 */

@Slf4j
@Service
public class KafkaConsumerService {


    private KafkaProducerService kafkaProducerService;

    @Autowired
    private ObjectMapper objectMapper;

    public KafkaConsumerService(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    /**
     * Consumes messages from the configured Kafka topic.
     * This method is automatically triggered when a new message is published to the topic.
     *
     * @param messages the list of message content received from the Kafka topic
     */
    @KafkaListener(topics = "${spring.apigateway.kafka.consumer.topic}", groupId = "${spring.apigateway.kafka.consumer.group-id}",
            concurrency = "5", containerFactory = "kafkaListenerContainerFactory")
    public void consume(List<String> messages) {

        try {
            LocalizationMessageEvent event = objectMapper.readValue(messages.get(0), LocalizationMessageEvent.class);
            log.info("Consumed Kafka message: {}", event);

            // Optionally forward to a producer (if needed)
            // kafkaProducerService.sendProcessedEvent(event);

        } catch (JsonParseException e) {
            log.error("Failed to parse message: {}", messages.get(0), e);
        } catch (Exception e) {
            log.error("Unexpected error while processing message: {}", messages.get(0), e);
        }
    }
}

