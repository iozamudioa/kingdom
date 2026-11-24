package dev.ioza.kingdom.bastion.application.registry;

import java.util.Objects;
import dev.ioza.kingdom.bastion.domain.ports.out.TavernRegistryPort;
import dev.ioza.kingdom.bastion.domain.ports.out.TavernPersistencePort;
import dev.ioza.kingdom.bastion.domain.ports.out.GatewayRoutesPort;
import dev.ioza.kingdom.contracts.TavernManifest;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class RegisterTavernService {

    private final TavernRegistryPort registry;
    private final TavernPersistencePort persistence;
    private final GatewayRoutesPort routes;

    public RegisterTavernService(TavernRegistryPort registry,
            TavernPersistencePort persistence, GatewayRoutesPort routes) {
        this.registry = registry;
        this.persistence = persistence;
        this.routes = routes;
    }

    public Mono<Void> register(TavernManifest tavern) {
        if (tavern == null || tavern.instanceId() == null || tavern.instanceId().isBlank()
                || tavern.target() == null || tavern.routePredicates() == null
                || tavern.routePredicates().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Invalid tavern manifest"));
        }
        return persistence.save(tavern).doOnNext(saved -> {
            boolean changed = !Objects.equals(registry.find(saved.instanceId()).orElse(null), saved);
            registry.save(saved);
            if (changed) routes.refresh();
            log.debug("Tavern registered: {}", saved.instanceId());
        }).then();
    }
}
