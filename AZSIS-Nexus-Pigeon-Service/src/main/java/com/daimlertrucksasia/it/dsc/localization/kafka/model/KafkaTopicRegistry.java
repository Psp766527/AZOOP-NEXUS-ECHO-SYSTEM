package com.daimlertrucksasia.it.dsc.localization.kafka.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * KafkaTopicRegistry is a JPA entity mapped to the PostgreSQL table {@code kafka_topic_registry}.
 *
 * <p>This registry enables:</p>
 * <ul>
 *     <li>Dynamic Kafka topic discovery at runtime</li>
 *     <li>Multi-tenant and client-specific topic routing</li>
 *     <li>Decoupling of services via centralized topic mapping</li>
 *     <li>Administrative control over topic routing without code changes</li>
 * </ul>
 *
 * <p>The table enforces a compound unique constraint on {@code serviceCoRelationID} and {@code topicName}
 * to ensure uniqueness of topic mappings per service.</p>
 */
@Entity
@Table(name = "kafka_topic_registry", uniqueConstraints = {
        @UniqueConstraint(name = "uk_service_topic", columnNames = {"service_co_relation_id", "topic_name"})
}, indexes = {
        @Index(name = "idx_service_topic", columnList = "service_co_relation_id, topic_name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaTopicRegistry {

    /**
     * Primary key identifier for this registry entry.
     * Automatically generated as a UUID by PostgreSQL.
     */
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /**
     * Logical identifier of the service associated with this topic mapping.
     * Typically, represents a microservice name (e.g., "order-service", "inventory-consumer").
     */
    @Column(name = "service_co_relation_id", nullable = false)
    private String serviceCoRelationID;

    /**
     * Runtime-only user correlation identifier used for in-memory filtering or multi-tenant logic.
     *
     * <p><b>Note:</b> This field is not persisted in the database.</p>
     */
    @Transient
    @JsonIgnore
    private String userCoRelationID;

    /**
     * Fully qualified Kafka topic name to route messages to.
     * This must be unique per service (enforced via compound unique constraint).
     */
    @Column(name = "topic_name", nullable = false)
    private String topicName;

    /**
     * Optional Kafka topic partition number for targeted routing.
     * Enables fine-grained control to maintain ordering or balance load.
     */
    @Column(name = "topic_partition_no")
    private Integer topicPartitionNo;

    /**
     * Optional human-readable description of this topic mapping.
     * Useful for admin or devops documentation and diagnostics.
     */
    @Column(name = "description")
    private String description;
}
