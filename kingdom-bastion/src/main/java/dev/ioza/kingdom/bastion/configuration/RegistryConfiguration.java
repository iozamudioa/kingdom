package dev.ioza.kingdom.bastion.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import dev.ioza.kingdom.bastion.adapters.out.registry.InMemoryTavernRegistry;
import dev.ioza.kingdom.bastion.adapters.out.gateway.SpringGatewayRoutesAdapter;
import dev.ioza.kingdom.bastion.adapters.out.gateway.TavernRouteDefinitionRepository;
import dev.ioza.kingdom.bastion.domain.ports.out.GatewayRoutesPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import dev.ioza.kingdom.bastion.application.registry.RegisterTavernService;
import dev.ioza.kingdom.bastion.domain.ports.out.TavernRegistryPort;

@Configuration
public class RegistryConfiguration {

    @Bean
    TavernRegistryPort tavernRegistryPort() {
        return new InMemoryTavernRegistry();
    }

    @Bean
    RouteDefinitionRepository tavernRouteDefinitionRepository(TavernRegistryPort registry) {
        return new TavernRouteDefinitionRepository(registry);
    }

    @Bean
    GatewayRoutesPort gatewayRoutesPort(ApplicationEventPublisher publisher) {
        return new SpringGatewayRoutesAdapter(publisher);
    }

    @Bean
    RegisterTavernService registerTavernUseCase(TavernRegistryPort registry,
            GatewayRoutesPort gatewayRoutesPort) {
        return new RegisterTavernService(registry, gatewayRoutesPort);
    }

}
