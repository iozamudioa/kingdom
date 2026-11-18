package dev.ioza.kingdom.tavern.host.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kingdom.tavern.host")
public record TavernHostProperties(Mode mode, String source) {
}
