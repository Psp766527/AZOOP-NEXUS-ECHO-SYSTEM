package com.daimlertrucksasia.it.dsc.localization.kafka.infra;

import com.daimlertrucksasia.it.dsc.localization.kafka.model.KafkaTopicRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for accessing and managing {@link KafkaTopicRegistry} entities in PostgreSQL.
 *
 * <p>This repository supports CRUD operations for Kafka topic mappings that are used for
 * dynamic topic resolution in a distributed microservices' environment.</p>
 *
 * <h3>Use Cases:</h3>
 * <ul>
 *   <li>Resolve Kafka topic names dynamically at runtime using {@code serviceCoRelationID}</li>
 *   <li>Store and manage partition-based routing logic for specific services</li>
 *   <li>Support multi-tenancy and client-specific topic resolution patterns</li>
 *   <li>Enable admin-level topic mapping configuration without redeploying services</li>
 * </ul>
 *
 * <h3>Best Practices:</h3>
 * <ul>
 *   <li>Use this repository from a dedicated service class like {@code TopicDiscoveryService}</li>
 *   <li>Ensure uniqueness of combinations like {@code serviceCoRelationID + topicName}
 *       via compound indexes (already defined in the entity)</li>
 *   <li>Extend this interface with custom queries if advanced resolution is needed
 *       (e.g., filtering by additional metadata)</li>
 * </ul>
 *
 */
@Repository
public interface TopicDiscoveryRepo extends JpaRepository<KafkaTopicRegistry, UUID> {

    @Query("SELECT k FROM KafkaTopicRegistry k WHERE k.serviceCoRelationID = :serviceCoRelationID")
    Optional<KafkaTopicRegistry> findTopicByServiceCoRelationID(String serviceCoRelationID);
}
