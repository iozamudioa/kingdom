package dev.ioza.kingdom.tavern.lock.infrastructure.properties;

import java.net.URI;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kingdom.tavern.lock")
public record TavernLockProperties(

        boolean enabled,

        URI issuer,

        URI royalArchivesUri) {

    public TavernLockProperties {

        if (issuer == null) {
            throw new IllegalArgumentException("kingdom.tavern.lock.issuer is required");
        }

        if (royalArchivesUri == null) {
            throw new IllegalArgumentException(
                    "kingdom.tavern.lock.royal-archives-uri is required");
        }
    }
}
