package com.daimlertrucksasia.it.dsc.localization.kafka.service.consumer;

import com.daimlertrucksasia.it.dsc.localization.exceptions.InvalidEventTypeException;
import com.daimlertrucksasia.it.dsc.localization.kafka.model.LocalizationMessageEvent;
import com.daimlertrucksasia.it.dsc.localization.kafka.service.TopicDiscoveryService;
import com.daimlertrucksasia.it.dsc.localization.kafka.service.producer.KafkaProducerService;
import com.daimlertrucksasia.it.dsc.localization.localization.infra.MessageEventAuditEntryRepo;
import com.daimlertrucksasia.it.dsc.localization.localization.model.MessageEventAuditEntity;
import com.daimlertrucksasia.it.dsc.localization.localization.service.MessageService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
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


    private final MessageService messageService;

    private final MessageEventAuditEntryRepo auditEntryRepo;

    private final KafkaProducerService kafkaProducerService;

    private final TopicDiscoveryService topicDiscoveryService;

    public KafkaConsumerService(MessageService messageService, KafkaProducerService kafkaProducerService,
                                MessageEventAuditEntryRepo auditEntryRepo, TopicDiscoveryService topicDiscoveryService) {
        this.messageService = messageService;
        this.auditEntryRepo = auditEntryRepo;
        this.topicDiscoveryService = topicDiscoveryService;
        this.kafkaProducerService = kafkaProducerService;
    }

    /**
     * Consumes messages from the configured Kafka topic.
     * This method is automatically triggered when a new message is published to the topic.
     *
     * @param messages the list of message content received from the Kafka topic
     */
    @KafkaListener(topics = "${spring.localization.kafka.consumer.topic}", groupId = "${spring.localization.kafka.consumer.group-id}",
            concurrency = "5", containerFactory = "kafkaListenerContainerFactory")
    public void consume(List<String> messages) {

        List<MessageEventAuditEntity> saveAllAuditLogsInBatch = new ArrayList<>();

        messages.forEach(message -> {
            try {

                ObjectMapper jsonObjectMapper = new ObjectMapper();
                LocalizationMessageEvent event = jsonObjectMapper.readValue(message, LocalizationMessageEvent.class);

                String convertedMsg = messageService.getMessage(event.getMsgTemplateID(), event.getArgs() != null ?
                        event.getArgs().toArray(new String[0]) : new Object[]{}, Locale.ENGLISH);

                //Building Event Audit for auditing purpose.
                MessageEventAuditEntity eventAuditEntry = MessageEventAuditEntity.builder()
                        .requestId(event.getRequestId())
                        .userCoRelationID(event.getUserCoRelationID())
                        .serviceCoRelationID(event.getServiceCoRelationID())
                        .msgTemplateID(event.getMsgTemplateID())
                        .args(String.join(",", event.getArgs()))
                        .content(event.getContent())
                        .locale(event.getLocale())
                        .resolvedMessage(convertedMsg)
                        .msgCreationTimestamp(Instant.parse(event.getMsgCreationTimestamp()))
                        .msgResolutionTimestamp(Instant.parse(Instant.now().getEpochSecond() + "")).build();

                saveAllAuditLogsInBatch.add(eventAuditEntry);
                event.setResolvedMessage(convertedMsg);
                event.setMsgResolutionTimestamp(Instant.now().getEpochSecond() + "");
                kafkaProducerService.sendMsg(topicDiscoveryService.resolveTopic(event.getServiceCoRelationID()), event.getRequestId(), event.toString());

            } catch (JsonParseException jsonParseException) {
                log.error("Required Event of Type Message but received invalid Event {}", jsonParseException.getMessage());
                kafkaProducerService.sendMsg("", "", "");
                throw new InvalidEventTypeException("Required Event of Type Message but received invalid Event" + jsonParseException.getMessage());
            } catch (Exception ex) {
                log.error("Error processing Kafka message: {} ", ex.getMessage());
                kafkaProducerService.sendMsg("", "", "");
                throw new InvalidEventTypeException("Unknown exception while parsing event to model" + ex.getMessage());
            }
        });


        //TODO: try not to overload the db query for filtering the duplicate requestIDs instead try to implement new approach

        List<String> existingIds = auditEntryRepo.findAllByRequestIdIn(saveAllAuditLogsInBatch.stream()
                        .map(MessageEventAuditEntity::getRequestId)
                        .toList())
                .stream()
                .map(MessageEventAuditEntity::getRequestId)
                .toList();

        List<MessageEventAuditEntity> newEntries = saveAllAuditLogsInBatch.stream()
                .filter(messageEventUnit -> !existingIds.contains(messageEventUnit.getRequestId()))
                .toList();

        if (auditEntryRepo.count() > 0)
            auditEntryRepo.saveAll(newEntries);

    }
}
