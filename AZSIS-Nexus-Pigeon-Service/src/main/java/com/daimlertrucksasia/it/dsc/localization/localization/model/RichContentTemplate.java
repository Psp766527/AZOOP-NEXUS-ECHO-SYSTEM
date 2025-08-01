package com.daimlertrucksasia.it.dsc.localization.localization.model;

import com.daimlertrucksasia.it.dsc.localization.localization.model.valueObjects.TemplateType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

/**
 * Entity representing a rich content template used in dynamic message rendering.
 *
 * <p>
 * This template supports HTML-based content with placeholders, versioning,
 * styling, and optional scripting. Useful for complex messages such as
 * email bodies, modal popups, and in-app notifications.
 * </p>
 *
 * <p>
 * The database enforces a unique constraint on {@code richContentTemplateID}
 * to avoid duplication across different services or locales.
 * </p>
 *
 * <p>
 * {@code providerID} and {@code productID} are transient fields used only at runtime
 * for internal mapping logic or external enrichment and are not stored in the database.
 * </p>
 *
 * @since 1.0
 */
@Entity
@Table(name = "rich_content_template", uniqueConstraints = {
        @UniqueConstraint(name = "uk_rich_template_id", columnNames = "rich_content_template_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RichContentTemplate {

    /**
     * Auto-generated primary key for the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Globally unique business identifier for this rich content template.
     * Used to reference and reuse templates across localized messages.
     */
    @Column(name = "rich_content_template_id", nullable = false, unique = true)
    private String richContentTemplateID;

    /**
     * Optional type classification for the template (e.g., ALERT_POPUP, TOAST).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "template_type")
    private TemplateType templateType;

    /**
     * HTML content with dynamic placeholders (e.g., ${username}, ${vehicleId}).
     * Intended to be rendered or substituted at runtime.
     */
    @Column(name = "html_content", columnDefinition = "TEXT")
    private String htmlContent;

    /**
     * A set of expected placeholder variable names to be resolved dynamically.
     * Stored as a comma-separated string in PostgreSQL.
     */
    @ElementCollection
    @CollectionTable(name = "template_placeholders", joinColumns = @JoinColumn(name = "template_id"))
    @Column(name = "placeholder")
    private Set<String> placeholders;

    /**
     * Optional CSS content or styles used in rendering.
     */
    @Column(name = "style", columnDefinition = "TEXT")
    private String style;

    /**
     * Optional version string (semantic or incremental) for template versioning.
     */
    @Column(name = "version")
    private String version;

    /**
     * Optional JavaScript logic used in template rendering or previewing.
     */
    @Column(name = "script", columnDefinition = "TEXT")
    private String script;

    /**
     * Runtime-only field representing the mapping for service provider ID.
     * Not persisted to the database.
     */
    @Transient
    @JsonIgnore
    private String providerID;

    /**
     * Runtime-only field representing the mapping for product/application ID.
     * Not persisted to the database.
     */
    @Transient
    @JsonIgnore
    private String productID;
}
