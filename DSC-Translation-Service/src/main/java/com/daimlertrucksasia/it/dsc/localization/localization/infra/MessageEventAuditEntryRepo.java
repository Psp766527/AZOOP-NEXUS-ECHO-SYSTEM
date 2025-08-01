package com.daimlertrucksasia.it.dsc.localization.localization.infra;

import com.daimlertrucksasia.it.dsc.localization.localization.model.MessageEventAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for performing CRUD and custom query operations on
 * {@link MessageEventAuditEntity} records stored in PostgreSQL.
 *
 * <p>
 * This interface extends Spring Data JPA's {@link JpaRepository}, providing
 * standard data access methods for saving, deleting, and querying audit entries.
 * </p>
 *
 * <h2>Use Case:</h2>
 * <ul>
 *   <li>Support audit trails for localization and messaging workflows</li>
 *   <li>Enable efficient querying of logs by {@code requestId} using batch lookup</li>
 *   <li>Allow developers and administrators to trace message flow across microservices</li>
 * </ul>
 *
 * @see MessageEventAuditEntity
 */
@Repository
public interface MessageEventAuditEntryRepo extends JpaRepository<MessageEventAuditEntity, Long> {

    /**
     * Retrieves all audit entries whose {@code requestId} is included in the provided list.
     *
     * <p>
     * This method enables efficient batch retrieval of audit records using SQL's {@code IN} clause.
     * Commonly used in log analysis, traceability debugging, or bulk reporting tools.
     * </p>
     *
     * @param requestIds List of request IDs to search for
     * @return List of {@link MessageEventAuditEntity} records matching the input IDs
     */
    List<MessageEventAuditEntity> findAllByRequestIdIn(List<String> requestIds);
}
