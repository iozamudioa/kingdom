package dev.ioza.kingdom.bastion.domain.ports.out;

import dev.ioza.kingdom.bastion.domain.model.identity.KnightSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface KnightSessionPort {

    Mono<KnightSession> save(KnightSession knightSession);

    Mono<KnightSession> find(String sessionId);

    Mono<Void> destroy(String sessionId);

    Flux<KnightSession> findByRealmSessionId(String realmSessionId);

    Flux<KnightSession> findByKnightId(String knightId);

}
