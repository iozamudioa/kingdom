package dev.ioza.kingdom.bastion.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import dev.ioza.kingdom.bastion.adapters.out.registry.InMemoryTavernRegistry;
import dev.ioza.kingdom.bastion.application.registry.RegisterTavernService;
import dev.ioza.kingdom.bastion.domain.ports.out.TavernRegistryPort;

@Configuration
public class RegistryConfiguration {

    @Bean
    TavernRegistryPort tavernRegistryPort() {
        return new InMemoryTavernRegistry();
    }

    @Bean
    RegisterTavernService registerTavernUseCase(TavernRegistryPort tavernRegistryPort
    // ,GatewayRoutesPort gatewayRoutesPort
    ) {
        return new RegisterTavernService(tavernRegistryPort);
    }

}
