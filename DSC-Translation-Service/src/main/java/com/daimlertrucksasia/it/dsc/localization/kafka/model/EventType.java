package com.daimlertrucksasia.it.dsc.localization.kafka.model;

/**
 * {@code EventType} serves as a sealed base class in the Kafka message model hierarchy.
 * <p>
 * This class is designed to represent the common abstraction for various event message types
 * exchanged within the system. By using Java's sealed class feature, it restricts which
 * subclasses can extend this class, enforcing a well-defined inheritance structure.
 * </p>
 *
 * <p>Key benefits of using a sealed class here include:</p>
 * <ul>
 *   <li><strong>Type safety</strong>: Only known and explicitly declared subclasses are permitted, making it easier to manage event types.</li>
 *   <li><strong>Extensibility control</strong>: Prevents external or unintended classes from extending the base event model.</li>
 *   <li><strong>Better pattern matching support</strong>: Sealed types work well with enhanced switch statements and pattern matching features introduced in newer Java versions.</li>
 * </ul>
 *
 * <p>
 * This class is currently only extended by:
 * <ul>
 *   <li>{@link LocalizationMessageEvent} - which models a localization-specific event containing message template and localization metadata.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Typical use cases for {@code EventType} and its subclasses include:
 * <ul>
 *   <li>Publishing and consuming structured Kafka messages</li>
 *   <li>Event-driven service-to-service communication</li>
 *   <li>Extending for domain-specific event contracts</li>
 * </ul>
 * </p>
 *
 * <p>Future subclasses may be added (and permitted) as the system expands to cover more message types.</p>
 *
 * @see LocalizationMessageEvent
 * @since 1.0
 */
public sealed class EventType permits LocalizationMessageEvent {
}
