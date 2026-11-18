package dev.ioza.kingdom.bastion.adapters.out.knight;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import dev.ioza.kingdom.bastion.domain.model.identity.KnightSession;
import dev.ioza.kingdom.bastion.domain.ports.out.KnightSessionPort;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class InMemoryKnightSessionAdapter implements KnightSessionPort {

    private final Map<String, KnightSession> sessions = new ConcurrentHashMap<>();

    @Override
    public Mono<KnightSession> save(KnightSession knightSession) {

        String sessionId = knightSession.passport();

        sessions.put(sessionId, knightSession);

        log.info("Knight session created {}", sessionId);;

        return Mono.just(knightSession);
    }

    @Override
    public Mono<KnightSession> find(String sessionId) {

        log.info("Knight session found {}", sessionId);

        return Mono.justOrEmpty(sessions.get(sessionId));

    }

    @Override
    public Mono<Void> destroy(String sessionId) {

        log.info("Knight session destroyed {}", sessionId);

        if (sessionId != null) {
            sessions.remove(sessionId);
        }

        return Mono.empty();

    }

    @Override
    public Flux<KnightSession> findByRealmSessionId(String realmSessionId) {
        return Flux.fromIterable(sessions.values()).filter(KnightSession::isRealm)
                .filter(session -> realmSessionId.equals(session.realmSessionId()));
    }

    @Override
    public Flux<KnightSession> findByKnightId(String knightId) {
        return Flux.fromIterable(sessions.values()).filter(KnightSession::isRealm)
                .filter(session -> knightId.equals(session.knight().id()));
    }

}
