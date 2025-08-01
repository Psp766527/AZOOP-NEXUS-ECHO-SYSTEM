package com.daimlertrucksasia.it.dsc.localization.localization.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

/**
 * Configuration class for setting up internationalization (i18n) locale resolution.
 * <p>
 * This configuration uses {@link AcceptHeaderLocaleResolver} to determine the locale
 * based on the "Accept-Language" HTTP header from the client request.
 * If no locale is specified in the request, it falls back to {@link Locale#ENGLISH} as the default.
 *
 */
@Configuration
public class LocaleConfig {

    /**
     * Defines the {@link LocaleResolver} bean that determines the locale
     * from the "Accept-Language" header of incoming HTTP requests.
     * <p>
     * This bean enables Spring to resolve the appropriate locale for the request,
     * allowing localized responses when used in conjunction with message sources and locale-specific resources.
     *
     * @return a configured {@link AcceptHeaderLocaleResolver} with English as the default locale
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH); // fallback default
        return resolver;
    }
}
