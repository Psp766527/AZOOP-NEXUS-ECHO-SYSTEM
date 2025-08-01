package com.daimlertrucksasia.it.dsc.localization.localization.infra;

import com.daimlertrucksasia.it.dsc.localization.localization.model.LocalizedMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * JPA repository interface for accessing localized message entries from PostgreSQL.
 *
 * <p>This interface extends {@link JpaRepository} to provide standard CRUD operations
 * as well as custom queries for localized message retrieval.</p>
 *
 * <p>
 * The repository supports dynamic lookup of translated messages by message template
 * and locale, which is commonly used in internationalization (i18n) systems.
 * </p>
 *
 * @since 1.0
 */
@Repository
public interface MessageRepository extends JpaRepository<LocalizedMessageEntity, Long> {

    /**
     * Retrieves a localized message by the template identifier and locale.
     *
     * <p>
     * This method is equivalent to:
     * <pre>
     * SELECT message
     * FROM localized_message
     * WHERE msg_template_id = :msgTemplateID AND locale = :locale
     * </pre>
     * </p>
     *
     * @param msgTemplateID the unique message template key
     * @param locale        the locale string (e.g., "en_US", "de_DE")
     * @return the {@link LocalizedMessageEntity} containing only the message text,
     *         or {@code null} if not found
     */
    @Query("SELECT l FROM LocalizedMessageEntity l WHERE l.msgTemplateID = :msgTemplateID AND l.locale = :locale")
    LocalizedMessageEntity findMessageByCodeAndLocale(String msgTemplateID, String locale);
}
