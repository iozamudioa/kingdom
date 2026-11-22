package dev.ioza.kingdom.bastion.adapters.in.rest.audience.logout;

import dev.ioza.kingdom.bastion.domain.model.identity.KnightSession;
import dev.ioza.kingdom.bastion.domain.ports.out.KingdomPassportPort;
import dev.ioza.kingdom.bastion.domain.ports.out.KnightSessionPort;
import dev.ioza.kingdom.bastion.domain.ports.out.RealmPort;
import dev.ioza.kingdom.bastion.domain.ports.out.TavernRegistryPort;
import dev.ioza.kingdom.contracts.TavernManifest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.net.URI;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Shared logout endpoint for registered Kingdom UI taverns. Each tavern decides
 * its own post-logout returnTo URL and starts a fresh Audience flow afterwards.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class AudienceDestroyController {

    private final KnightSessionPort knightSessionPort;
    private final KingdomPassportPort kingdomPassportPort;
    private final RealmPort realmPort;
    private final TavernRegistryPort tavernRegistryPort;

    @PostMapping("/audience/destroy")
    public Mono<ResponseEntity<Void>> destroy(ServerWebExchange exchange) {
        String origin = exchange.getRequest().getHeaders().getOrigin();
        if (!isRegisteredUiOrigin(origin)) {
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
        }

        var cookie = exchange.getRequest().getCookies().getFirst("KINGDOM_PASSPORT");
        Mono<Void> invalidate = cookie == null ? Mono.empty()
                : knightSessionPort.find(cookie.getValue())
                        .flatMap(this::invalidateSession)
                        .then();

        return invalidate
                .onErrorResume(error -> {
                    log.warn("Kingdom logout encountered an error; clearing the browser passport", error);
                    return Mono.empty();
                })
                .then(Mono.fromSupplier(() -> ResponseEntity.noContent()
                        .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin)
                        .header(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
                        .header(HttpHeaders.VARY, HttpHeaders.ORIGIN)
                        .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, max-age=0")
                        .header(HttpHeaders.PRAGMA, "no-cache")
                        .header(HttpHeaders.SET_COOKIE, kingdomPassportPort.destroy().toString())
                        .<Void>build()));
    }

    private boolean isRegisteredUiOrigin(String origin) {
        if (origin == null) {
            return false;
        }
        try {
            URI uri = URI.create(origin);
            if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null
                    || uri.getPort() != -1 || uri.getRawUserInfo() != null
                    || uri.getRawPath() != null && !uri.getRawPath().isEmpty()
                    || uri.getRawQuery() != null || uri.getRawFragment() != null) {
                return false;
            }
            return tavernRegistryPort.findAll().stream()
                    .filter(tavern -> tavern.capabilities().contains("ui"))
                    .anyMatch(tavern -> matchesHost(tavern, uri.getHost()));
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private boolean matchesHost(TavernManifest tavern, String host) {
        return tavern.routePredicates().stream()
                .anyMatch(predicate -> "Host".equalsIgnoreCase(predicate.name())
                        && host.equalsIgnoreCase(predicate.value()));
    }

    private Mono<Void> invalidateSession(KnightSession session) {
        Mono<Void> realmLogout = session.isRealm() && session.refreshToken() != null
                ? realmPort.logout(session.refreshToken())
                        .onErrorResume(error -> {
                            log.warn("Keycloak logout failed; invalidating local Kingdom session", error);
                            return Mono.empty();
                        })
                : Mono.empty();

        return realmLogout.then(knightSessionPort.destroy(session.passport()));
    }
}
