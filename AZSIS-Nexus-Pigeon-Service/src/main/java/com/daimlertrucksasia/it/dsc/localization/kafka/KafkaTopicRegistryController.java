package com.daimlertrucksasia.it.dsc.localization.kafka;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.daimlertrucksasia.it.dsc.localization.kafka.model.KafkaTopicRegistry;
import com.daimlertrucksasia.it.dsc.localization.kafka.infra.TopicDiscoveryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST Controller for managing {@link KafkaTopicRegistry} entries.
 * <p>
 * This controller provides a complete set of CRUD operations to allow
 * dynamic creation, retrieval, update, and deletion of Kafka topic routing
 * configurations stored in MongoDB.
 * </p>
 *
 * <p>Base URL path: <code>/kafka/topics</code></p>
 *
 * <p>Common use cases:</p>
 * <ul>
 *   <li>Allow dynamic Kafka topic discovery based on service correlation</li>
 *   <li>Support multitenancy and service-specific topic mappings</li>
 *   <li>Provide admin tooling via REST to manage topic routing</li>
 * </ul>
 *
 */
@RestController
@RequestMapping("/kafka/topics")
@RequiredArgsConstructor
public class KafkaTopicRegistryController {

    private final TopicDiscoveryRepo topicDiscoveryRepo;

    /**
     * Creates a new topic registry document and saves it to MongoDB.
     *
     * @param registry The {@link KafkaTopicRegistry} request body containing new topic mapping details
     * @return ResponseEntity with saved entity and HTTP 200 status
     */
    @PostMapping
    public ResponseEntity<KafkaTopicRegistry> createRegistry(@RequestBody KafkaTopicRegistry registry) {
        return ResponseEntity.ok(topicDiscoveryRepo.save(registry));
    }

    /**
     * Retrieves all topic registry entries from MongoDB.
     *
     * @return ResponseEntity containing the list of all {@link KafkaTopicRegistry} entries
     */
    @GetMapping
    public ResponseEntity<List<KafkaTopicRegistry>> getAll() {
        return ResponseEntity.ok(topicDiscoveryRepo.findAll());
    }

    /**
     * Fetches a topic registry document by its MongoDB ObjectId.
     *
     * @param id The {@link Long} of the topic registry document
     * @return ResponseEntity with matching entry or HTTP 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<KafkaTopicRegistry> getById(@PathVariable String id) {
        return topicDiscoveryRepo.findById(UUID.fromString(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Finds a topic registry based on the serviceCoRelationID.
     *
     * @param serviceCoRelationID Logical identifier of the service
     * @return ResponseEntity with the topic mapping or HTTP 404 if not found
     */
    @GetMapping("/service/{serviceCoRelationID}")
    public ResponseEntity<KafkaTopicRegistry> findByServiceCoRelationID(@PathVariable String serviceCoRelationID) {
        Optional<KafkaTopicRegistry> result = topicDiscoveryRepo.findTopicByServiceCoRelationID(serviceCoRelationID);
        return result.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Updates an existing registry document based on its MongoDB ObjectId.
     *
     * @param id      ObjectId of the document to update
     * @param updated Request body containing updated values
     * @return ResponseEntity with updated entry or HTTP 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<KafkaTopicRegistry> update(@PathVariable String id,
                                                     @RequestBody KafkaTopicRegistry updated) {
        return topicDiscoveryRepo.findById(UUID.fromString(id))
                .map(existing -> {
                    updated.setId(UUID.fromString(id)); // Ensure ID is preserved during update
                    KafkaTopicRegistry saved = topicDiscoveryRepo.save(updated);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes a topic registry entry by its ObjectId.
     *
     * @param id ObjectId of the document to delete
     * @return HTTP 204 if deleted, or HTTP 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (topicDiscoveryRepo.existsById(UUID.fromString(id))) {
            topicDiscoveryRepo.deleteById(UUID.fromString(id));
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
