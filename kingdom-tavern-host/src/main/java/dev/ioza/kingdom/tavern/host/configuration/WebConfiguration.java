package dev.ioza.kingdom.tavern.host.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnProperty(prefix = "kingdom.tavern.host", name = "mode", havingValue = "STATIC")

public class WebConfiguration implements WebMvcConfigurer {

    private final TavernHostProperties properties;

    public WebConfiguration(TavernHostProperties properties) {
        this.properties = properties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/**")
                .addResourceLocations("file:" + normalize(properties.source()));

    }

    private String normalize(String documentRoot) {

        String normalized = documentRoot;

        if (!normalized.endsWith("/")) {
            normalized += "/";
        }

        return normalized;

    }

}
