package dev.ioza.kingdom.tavern.emissary.infrastructure.properties;

import java.net.URI;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kingdom.tavern.emissary")
public record TavernEmissaryProperties(

        boolean enabled,

        String bastionUrl,

        String username,

        String password,

        String tavernName,

        URI target,

        String healthPath,

        List<String> capabilities,

        List<String> publicPaths,

        String domain,

        String path) {

    public TavernEmissaryProperties {
        healthPath = defaultValue(healthPath, "/internal/kingdom/health");
        capabilities = capabilities == null ? List.of() : List.copyOf(capabilities);
        publicPaths = publicPaths == null ? List.of() : List.copyOf(publicPaths);
    }

    private static String defaultValue(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

}
