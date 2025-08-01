package com.daimlertrucksasia.it.dsc.localization.kafka.service;

import com.daimlertrucksasia.it.dsc.localization.kafka.infra.TopicDiscoveryRepo;
import com.daimlertrucksasia.it.dsc.localization.kafka.model.KafkaTopicRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * {@code TopicDiscoveryService} is a service component responsible for resolving
 * Kafka topic names dynamically based on a provided {@code serviceCoRelationID}.
 *
 * <p>This service is designed to abstract the lookup of Kafka topic configurations
 * stored in MongoDB (via the {@link KafkaTopicRegistry} entity), enabling runtime
 * flexibility in how services route messages through Kafka.</p>
 *
 * <p>Typical use cases include:
 * <ul>
 *   <li>Dynamically resolving topics for multi-tenant or client-specific communication</li>
 *   <li>Centralized topic management without modifying application configuration</li>
 *   <li>Decoupling topic naming conventions from service logic</li>
 * </ul>
 *
 * <p>If no matching topic is found for the given {@code serviceCoRelationID}, a fallback
 * default topic name is returned to ensure robustness in message routing.</p>
 *
 * <p>This service can be further extended to support partition-aware messaging by
 * incorporating Kafka consumer or producer logic using Kafka's {@code assign()} method.</p>
 *
 * @see KafkaTopicRegistry
 * @see TopicDiscoveryRepo
 */
@Service
@RequiredArgsConstructor
public class TopicDiscoveryService {

    /**
     * Repository interface for performing topic lookup operations from the MongoDB collection.
     */
    private final TopicDiscoveryRepo topicDiscoveryRepo;

    /**
     * Resolves the Kafka topic name associated with a given service correlation identifier.
     *
     * <p>This method queries the MongoDB-backed {@link KafkaTopicRegistry} collection to fetch
     * a registered topic for the given {@code serviceCoRelationID}. If no record is found,
     * a default topic name {@code "Default-Topic-Tracker"} is returned instead.</p>
     *
     * <p>This method caches results in Redis with serviceCoRelationID as the cache key.
     * Subsequent calls with the same key will return cached values to improve performance.</p>
     *
     * @param serviceCoRelationID The correlation ID of the service initiating or consuming messages.
     * @return The resolved topic name, or a fallback default topic name if not found.
     */
    @Cacheable(value = "serviceCoRelationID", key = "#serviceCoRelationID")
    public String resolveTopic(String serviceCoRelationID) {
        Optional<KafkaTopicRegistry> kafkaTopicRegistry =
                topicDiscoveryRepo.findTopicByServiceCoRelationID(serviceCoRelationID);

        return kafkaTopicRegistry
                .map(KafkaTopicRegistry::getTopicName)
                .orElse("Default-Topic-Tracker");
    }

    /**
     * Placeholder method for future support of partition-specific message publishing
     * and consumption using Kafka's {@code assign()} API.
     *
     * <p>This is useful when precise control over topic partitions is required
     * — such as message ordering, load distribution, or fault isolation.</p>
     *
     * <p>Example scenarios:
     * <ul>
     *     <li>Routing a message to a specific Kafka partition based on user ID or session ID</li>
     *     <li>Manually assigning consumer partitions instead of relying on consumer group balancing</li>
     * </ul>
     * </p>
     */
    // TODO: Implementation required for partition-specific msg publishing and processing using assign() method of Kafka
}
