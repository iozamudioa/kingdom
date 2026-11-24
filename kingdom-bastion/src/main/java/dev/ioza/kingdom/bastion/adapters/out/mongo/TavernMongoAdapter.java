package dev.ioza.kingdom.bastion.adapters.out.mongo;

import java.time.Instant;
import org.springframework.stereotype.Component;
import dev.ioza.kingdom.bastion.adapters.out.mongo.documents.TavernDoc;
import dev.ioza.kingdom.bastion.adapters.out.mongo.repositories.TavernMongoRepository;
import dev.ioza.kingdom.bastion.domain.ports.out.TavernPersistencePort;
import dev.ioza.kingdom.contracts.TavernManifest;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TavernMongoAdapter implements TavernPersistencePort {

    private final TavernMongoRepository repository;

    @Override
    public Mono<TavernManifest> save(TavernManifest manifest) {
        Instant now = Instant.now();
        return repository.findById(manifest.instanceId())
                .map(existing -> existing.registeredAt())
                .defaultIfEmpty(now)
                .flatMap(registered -> repository.save(
                        new TavernDoc(manifest.instanceId(), manifest, registered, now)))
                .map(TavernDoc::manifest);
    }

    @Override
    public Flux<TavernManifest> findAll() {
        return repository.findAll().map(TavernDoc::manifest);
    }
}
