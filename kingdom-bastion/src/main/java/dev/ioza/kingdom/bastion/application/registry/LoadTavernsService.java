package dev.ioza.kingdom.bastion.application.registry;

import java.util.concurrent.atomic.AtomicInteger;
import dev.ioza.kingdom.bastion.adapters.out.mongo.repositories.TavernDefinitionMongoRepository;
import dev.ioza.kingdom.bastion.domain.model.registry.TavernDefinition;
import dev.ioza.kingdom.contracts.TavernManifest;
import dev.ioza.kingdom.bastion.adapters.out.registry.TavernPathRules;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import dev.ioza.kingdom.bastion.domain.ports.out.GatewayRoutesPort;
import dev.ioza.kingdom.bastion.domain.ports.out.TavernPersistencePort;
import dev.ioza.kingdom.bastion.domain.ports.out.TavernRegistryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoadTavernsService {
    private final TavernPersistencePort persistence;
    private final TavernRegistryPort registry;
    private final GatewayRoutesPort routes;
    private final TavernDefinitionMongoRepository definitions;

    @EventListener(ApplicationReadyEvent.class)
    public void restore() {
        AtomicInteger count = new AtomicInteger();
        persistence.findAll()
                .flatMap(manifest -> definitions.findById(manifest.tavernId())
                        .filter(def -> approved(manifest, def))
                        .doOnNext(def -> {
                            registry.save(manifest);
                            count.incrementAndGet();
                        }))
                .doOnComplete(() -> {
                    routes.refresh();
                    log.info("Restored {} taverns from MongoDB", count.get());
                })
                .subscribe(
                        ignored -> {},
                        ex -> log.error("Unable to restore taverns from MongoDB", ex));
    }
    private boolean approved(TavernManifest manifest, TavernDefinition definition) {
        return definition.enabled()
                && definition.domain().equalsIgnoreCase(TavernPathRules.domain(manifest))
                && definition.domainPath().equals(TavernPathRules.domainPath(manifest))
                && definition.context().equals(TavernPathRules.context(manifest))
                && definition.allowedAccess().equals(manifest.allowedAccess())
                && definition.capabilities().equals(manifest.capabilities())
                && definition.publicPaths().equals(manifest.publicPaths());
    }
}
