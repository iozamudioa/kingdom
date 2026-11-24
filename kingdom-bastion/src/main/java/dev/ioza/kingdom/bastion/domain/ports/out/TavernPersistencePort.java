package dev.ioza.kingdom.bastion.domain.ports.out;

import dev.ioza.kingdom.contracts.TavernManifest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TavernPersistencePort {
    Mono<TavernManifest> save(TavernManifest manifest);
    Flux<TavernManifest> findAll();
}
