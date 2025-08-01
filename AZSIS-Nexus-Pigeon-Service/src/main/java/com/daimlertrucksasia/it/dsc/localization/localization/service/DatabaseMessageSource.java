package com.daimlertrucksasia.it.dsc.localization.localization.service;

import com.daimlertrucksasia.it.dsc.localization.localization.infra.MessageRepository;
import com.daimlertrucksasia.it.dsc.localization.localization.model.LocalizedMessageEntity;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Optional;


/**
 * This Data Base Message Source will be used to retrieve locale specific message.
 */
@Service
public class DatabaseMessageSource extends AbstractMessageSource {

    private final MessageRepository messageRepository;

    public DatabaseMessageSource(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Builder
    public static DatabaseMessageSource build(MessageRepository messageRepository) {
        return new DatabaseMessageSource(messageRepository);
    }

    protected MessageFormat resolveCode(@NonNull String msgTemplateID, Locale locale) {
        return Optional.ofNullable(messageRepository.findMessageByCodeAndLocale(msgTemplateID, locale.toLanguageTag()))
                .map(msg -> new MessageFormat(msg.getMessage(), locale)).
                orElse(new MessageFormat("No message found under msgTemplateID [" + msgTemplateID + "] for locale '" + locale + "'"));
    }

    private String resolveMessage(String code, Locale locale) {
        String tag = locale.toLanguageTag();
        String lang = locale.getLanguage();

        LocalizedMessageEntity message = messageRepository.findMessageByCodeAndLocale(code, tag);
        if (message.getMessage() != null) return message.getMessage();

        if (!lang.equalsIgnoreCase(tag)) {
            message = messageRepository.findMessageByCodeAndLocale(code, lang);
            if (message.getMessage() != null) return message.getMessage();
        }

        return null;
    }

    private String format(String message, Object[] args, Locale locale) {
        return args == null || args.length == 0 ? message : new MessageFormat(message, locale).format(args);
    }

}
