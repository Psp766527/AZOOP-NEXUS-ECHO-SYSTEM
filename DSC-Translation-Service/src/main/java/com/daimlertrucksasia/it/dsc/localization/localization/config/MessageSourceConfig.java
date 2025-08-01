package com.daimlertrucksasia.it.dsc.localization.localization.config;

import com.daimlertrucksasia.it.dsc.localization.localization.infra.MessageRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up the custom {@link MessageSource} bean used in internationalization (i18n).
 * <p>
 * This configuration provides a {@link MessageSource} implementation that retrieves localized messages
 * from a database using a custom {@code DatabaseMessageSource}.
 * The message retrieval is backed by a {@link MessageRepository} which abstracts access to the data source.
 */
@Configuration
@SuppressWarnings("All")
public class MessageSourceConfig {

    /**
     * Defines a {@link MessageSource} bean that fetches localized messages from a database.
     * <p>
     * This bean enables Spring's i18n framework to resolve messages dynamically from the database
     * instead of traditional property files.
     *
     * @param messageRepository the repository used to access localized message records from the database
     * @return a configured {@link MessageSource} backed by {@code DatabaseMessageSource}
     */
    @Bean
    public MessageSource messageSource(MessageRepository messageRepository) {
        return com.daimlertrucksasia.it.dsc.localization.localization.service.DatabaseMessageSource.builder()
                .messageRepository(messageRepository)
                .build();
    }
}
