package dev.ioza.kingdom.bastion.adapters.in.rest.audience.logout;

import dev.ioza.kingdom.bastion.domain.model.identity.KnightSession;
import dev.ioza.kingdom.bastion.domain.ports.out.KingdomPassportPort;
import dev.ioza.kingdom.bastion.domain.ports.out.KnightSessionPort;
import dev.ioza.kingdom.bastion.domain.ports.out.RealmPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Browser-initiated Kingdom-wide logout. A navigation POST deliberately avoids
 * cross-origin fetch/CORS and lets Bastion clear its HttpOnly passport cookie.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class AudienceDestroyController {

    private static final String BANK_ORIGIN = "https://bank.ioza.dev";
    private static final String BASTION_ORIGIN = "https://bastion.ioza.dev";
    private static final String SIGNED_OUT_PAGE = """
            <!doctype html><html lang="es"><head><meta charset="utf-8">
            <meta name="viewport" content="width=device-width,initial-scale=1">
            <title>Sesión cerrada · Kingdom</title></head>
            <body style="font:16px system-ui;margin:12vh auto;padding:24px;max-width:500px">
            <h1>Sesión cerrada</h1><p>Tu sesión de Kingdom se ha cerrado.</p>
            <a href="https://bank.ioza.dev/dashboard">Iniciar sesión de nuevo en BANK</a>
            </body></html>
            """;

    private final KnightSessionPort knightSessionPort;
    private final KingdomPassportPort kingdomPassportPort;
    private final RealmPort realmPort;

    @PostMapping(value = "/audience/destroy", produces = MediaType.TEXT_HTML_VALUE)
    public Mono<ResponseEntity<String>> destroy(ServerWebExchange exchange) {
        String origin = exchange.getRequest().getHeaders().getOrigin();
        // A cross-site page must not be able to log a Knight out via a form POST.
        if (!BANK_ORIGIN.equals(origin) && !BASTION_ORIGIN.equals(origin)) {
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
                .then(Mono.fromSupplier(() -> ResponseEntity.ok()
                        .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, max-age=0")
                        .header(HttpHeaders.PRAGMA, "no-cache")
                        .header(HttpHeaders.SET_COOKIE, kingdomPassportPort.destroy().toString())
                        .contentType(MediaType.TEXT_HTML)
                        .body(SIGNED_OUT_PAGE)));
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
