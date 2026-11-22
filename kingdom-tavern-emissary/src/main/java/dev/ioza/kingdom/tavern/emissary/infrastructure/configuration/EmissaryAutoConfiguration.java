package dev.ioza.kingdom.tavern.emissary.infrastructure.configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import dev.ioza.kingdom.tavern.emissary.application.EmissaryService;
import dev.ioza.kingdom.tavern.emissary.application.port.out.BastionRegistryPort;
import dev.ioza.kingdom.tavern.emissary.domain.TavernManifestFactory;
import dev.ioza.kingdom.tavern.emissary.infrastructure.heartbeat.TavernHeartbeat;
import dev.ioza.kingdom.tavern.emissary.infrastructure.properties.TavernEmissaryProperties;
import dev.ioza.kingdom.tavern.emissary.infrastructure.registration.BastionRegistryHttpAdapter;

@AutoConfiguration
@EnableConfigurationProperties(TavernEmissaryProperties.class)
@ConditionalOnProperty(prefix = "kingdom.tavern.emissary", name = "enabled", havingValue = "true",
        matchIfMissing = true)
public class EmissaryAutoConfiguration {

    @Bean
    TavernManifestFactory tavernManifestFactory(TavernEmissaryProperties properties,
            Environment environment) {
        return new TavernManifestFactory(properties, environment);
    }

    @Bean
    TavernHeartbeat tavernHeartbeat(TavernManifestFactory manifestFactory,
            EmissaryService emissaryService, Environment environment) {
        return new TavernHeartbeat(manifestFactory, emissaryService, environment);
    }

    @Bean
    BastionRegistryPort bastionRegistryPort(TavernEmissaryProperties properties) {
        return new BastionRegistryHttpAdapter(properties.bastionUrl(), properties.username(),
                properties.password());
    }

    @Bean
    EmissaryService emissaryService(BastionRegistryPort bastionRegistryPort) {
        return new EmissaryService(bastionRegistryPort);
    }


}
