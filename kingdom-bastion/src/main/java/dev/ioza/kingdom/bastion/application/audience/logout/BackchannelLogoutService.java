package dev.ioza.kingdom.bastion.application.audience.logout;

import org.springframework.stereotype.Service;
import dev.ioza.kingdom.bastion.domain.model.identity.KnightSession;
import dev.ioza.kingdom.bastion.domain.model.realm.RealmLogout;
import dev.ioza.kingdom.bastion.domain.ports.in.BackchannelLogoutUseCase;
import dev.ioza.kingdom.bastion.domain.ports.out.KnightSessionPort;
import dev.ioza.kingdom.bastion.domain.ports.out.RealmLogoutTokenPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackchannelLogoutService implements BackchannelLogoutUseCase {


    private final RealmLogoutTokenPort realmLogoutTokenPort;
    private final KnightSessionPort knightSessionPort;

    @Override
    public Mono<Void> logout(String logoutToken) {

        return realmLogoutTokenPort.validate(logoutToken).flatMap(this::destroySessions);
    }

    private Mono<Void> destroySessions(RealmLogout logout) {

        if (logout.realmSessionId() != null) {
            return destroyByRealmSessionId(logout.realmSessionId());
        }

        if (logout.subject() != null) {
            return destroyBySubject(logout.subject());
        }

        return Mono.error(new IllegalArgumentException("Logout token does not contain sid or sub"));
    }

    private Mono<Void> destroyByRealmSessionId(String realmSessionId) {

        return destroy(knightSessionPort.findByRealmSessionId(realmSessionId), "realm session",
                realmSessionId);
    }

    private Mono<Void> destroyBySubject(String subject) {

        return destroy(knightSessionPort.findByKnightId(subject), "subject", subject);
    }

    private Mono<Void> destroy(Flux<KnightSession> sessions, String identifierType,
            String identifier) {

        return sessions.flatMap(session -> {
            log.info("Destroying Bastion passport {} by Realm {} {}", session.passport(),
                    identifierType, identifier);

            return knightSessionPort.destroy(session.passport());
        }).then();
    }

}
