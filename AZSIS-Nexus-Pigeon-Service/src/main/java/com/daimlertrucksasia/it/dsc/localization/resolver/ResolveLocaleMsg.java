package com.daimlertrucksasia.it.dsc.localization.resolver;

import com.daimlertrucksasia.it.dsc.localization.localization.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Service responsible for resolving localized messages based on message templates and locale.
 * <p>
 * This component acts as a wrapper around the {@link MessageService}, simplifying
 * the process of retrieving internationalized messages based on the given locale,
 * template ID, and dynamic arguments.
 * </p>
 *
 * <p>
 * Typically used in business logic or controller layers where localized messages
 * need to be generated for logging, exception messages, or client responses.
 * </p>
 *
 * <p>
 * This class is marked with {@link Service} so it can be managed as a Spring bean.
 * </p>
 *
 * <b>Example Usage:</b>
 * <pre>{@code
 * String message = resolveLocaleMsg.getResolvedMsg("staff.not.found", new Object[]{"John"}, Locale.ENGLISH);
 * }</pre>
 *
 * @since 1.0
 */
@Service
public class ResolveLocaleMsg {

    private final MessageService messageService;

    /**
     * Constructs a new {@code ResolveLocaleMsg} with the given message service dependency.
     *
     * @param messageService the message service used to resolve localized content
     */
    public ResolveLocaleMsg(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Resolves a localized message using the given template ID, arguments, and locale.
     *
     * @param msgTemplateId the ID of the message template
     * @param args          the dynamic arguments to be applied to the message template
     * @param locale        the target locale (e.g., {@code Locale.ENGLISH}, {@code Locale.JAPANESE})
     * @return the resolved message string, or a fallback if not found
     */
    public String getResolvedMsg(String msgTemplateId, Object[] args, Locale locale) {
        return messageService.getMessage(msgTemplateId, args, locale);
    }
}
