package com.example.ecommerceweb.configuration.i8n;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

@Configuration
public class LocalResolver extends AcceptHeaderLocaleResolver implements WebMvcConfigurer {

    @Override
    @NonNull
    public Locale resolveLocale(HttpServletRequest request) {
        String languageHeader = request.getHeader("Accept-Language");
        return StringUtils.hasText(languageHeader)
                ? Locale.lookup(Locale.LanguageRange.parse(languageHeader),
                List.of(new Locale("en-US"), new Locale("fr")))
                : Locale.getDefault();
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("message");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setCacheSeconds(3600);
        return messageSource;
    }
}
