package com.daimlertrucksasia.it.dsc.localization.localization.service;

import com.daimlertrucksasia.it.dsc.localization.localization.infra.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

/**
 * <p>
 * The MessageService class does serve the purpose of localizaiton of message based on the application access.
 * <p/>
 * @version 1.0
 */
@Service
public class MessageService {

    @Autowired
    private final MessageRepository messageRepository;

    @Autowired
    private final MessageSource messageSource;

    @Value("${localization.default-locale}")
    private String defaultLocale;

    public MessageService(MessageRepository messageRepository, MessageSource messageSource) {
        this.messageRepository = messageRepository;
        this.messageSource = messageSource;
    }


    /**
     * <p>
     * The GetMessage method is used to get localization specific message
     * <p/>
     *
     * @param msgTemplateID the Code
     * @param locale        the Locale
     * @return it returns the localized message per request based on
     */
    public String getMessage(String msgTemplateID, Locale locale) {
        return Optional.ofNullable(
                messageRepository.findMessageByCodeAndLocale(msgTemplateID, locale.toLanguageTag()).getMessage()
        ).orElse(messageRepository.findMessageByCodeAndLocale(msgTemplateID, defaultLocale).getMessage());

    }

    /**
     * <p>
     * The GetMessage method is used to get localization specific message
     * <p/>
     *
     * @param code   the Code
     * @param locale the Locale
     * @param args   the args
     * @return it returns the localized message per request based on
     */
    public String getMessage(String code, Object[] args, Locale locale) {
        return messageSource.getMessage(code, args, locale);
    }
}
