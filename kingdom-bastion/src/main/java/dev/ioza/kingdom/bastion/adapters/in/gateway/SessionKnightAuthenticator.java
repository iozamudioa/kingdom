package dev.ioza.kingdom.bastion.adapters.in.gateway;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import dev.ioza.kingdom.bastion.domain.model.parchment.Parchment;
import dev.ioza.kingdom.bastion.domain.ports.in.IssueParchmentUseCase;
import dev.ioza.kingdom.bastion.domain.ports.out.KnightSessionPort;
import dev.ioza.kingdom.bastion.domain.ports.out.RealmPort;
import dev.ioza.kingdom.contracts.TavernAccess;
import dev.ioza.kingdom.contracts.TavernManifest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class SessionKnightAuthenticator implements TavernAuthenticator {

        private final KnightSessionPort sessions;
        private final RealmPort realmPort;
        private final IssueParchmentUseCase issueKnightParchment;

        @Override
        public TavernAccess supports() {
                return TavernAccess.KNIGHT;
        }

        @Override
        public Mono<AuthenticationResult> authenticate(ServerWebExchange exchange,
                        TavernManifest tavern) {

                HttpCookie passport =
                                exchange.getRequest().getCookies().getFirst("KINGDOM_PASSPORT");

                if (passport == null) {
                        return Mono.just(new AuthenticationResult(
                                        AuthenticationStatus.NOT_AUTHENTICATED, exchange));
                }

                return sessions.find(passport.getValue())

                                .flatMap(knightSession -> {
                                        if (knightSession.isRealm() && knightSession.isAccessTokenExpired()) {
                                                log.info("Realm access token expired; destroying Kingdom session");
                                                Mono<Void> realmLogout = knightSession.refreshToken() == null
                                                                ? Mono.empty()
                                                                : realmPort.logout(knightSession.refreshToken())
                                                                                .onErrorResume(error -> {
                                                                                        log.warn("Keycloak expiry logout failed", error);
                                                                                        return Mono.empty();
                                                                                });
                                                return realmLogout.then(sessions.destroy(knightSession.passport()))
                                                                .thenReturn(new AuthenticationResult(
                                                                                AuthenticationStatus.NOT_AUTHENTICATED, exchange));
                                        }

                                        Parchment parchment = issueKnightParchment
                                                        .issue(knightSession.knight());

                                        ServerWebExchange mutated = exchange.mutate()
                                                        .request(request -> request
                                                                        .headers(headers -> headers
                                                                                        .setBearerAuth(parchment
                                                                                                        .value())))
                                                        .build();

                                        log.debug("Knight session valid. Issuing parchment for {}",
                                                        tavern.tavernName());

                                        return Mono.just(new AuthenticationResult(
                                                        AuthenticationStatus.AUTHENTICATED,
                                                        mutated));
                                })

                                .switchIfEmpty(Mono.fromSupplier(() -> {
                                        log.warn("Knight session not found");

                                        return new AuthenticationResult(
                                                        AuthenticationStatus.NOT_AUTHENTICATED,
                                                        exchange);
                                }));
        }

}
