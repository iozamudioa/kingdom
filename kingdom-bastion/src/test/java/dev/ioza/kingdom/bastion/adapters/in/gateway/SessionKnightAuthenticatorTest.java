package dev.ioza.kingdom.bastion.adapters.in.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import dev.ioza.kingdom.bastion.domain.model.identity.Knight;
import dev.ioza.kingdom.bastion.domain.model.identity.KnightSession;
import dev.ioza.kingdom.bastion.domain.model.parchment.Parchment;
import dev.ioza.kingdom.bastion.domain.ports.in.IssueParchmentUseCase;
import dev.ioza.kingdom.bastion.domain.ports.out.KnightSessionPort;
import dev.ioza.kingdom.contracts.TavernManifest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class SessionKnightAuthenticatorTest {
    private final KnightSessionPort sessions = mock(KnightSessionPort.class);
    private final IssueParchmentUseCase issuer = mock(IssueParchmentUseCase.class);
    private final SessionKnightAuthenticator authenticator =
            new SessionKnightAuthenticator(sessions, issuer);

    @Test
    void uiProxyReceivesParchment() {
        assertPropagation(List.of("ui"));
    }

    @Test
    void apiReceivesParchment() {
        assertPropagation(List.of("api"));
    }

    @Test
    void missingPassportDoesNotIssueParchment() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/test").build());
        StepVerifier.create(authenticator.authenticate(exchange, tavern(List.of("ui"))))
                .assertNext(result -> {
                    assertEquals(AuthenticationStatus.NOT_AUTHENTICATED, result.status());
                    assertNull(result.exchange().getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
                }).verifyComplete();
        verifyNoInteractions(sessions, issuer);
    }

    @Test
    void unknownPassportDoesNotIssueParchment() {
        when(sessions.find("unknown")).thenReturn(Mono.empty());
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/test")
                .cookie(new HttpCookie("KINGDOM_PASSPORT", "unknown")).build());
        StepVerifier.create(authenticator.authenticate(exchange, tavern(List.of("ui"))))
                .assertNext(result -> {
                    assertEquals(AuthenticationStatus.NOT_AUTHENTICATED, result.status());
                    assertNull(result.exchange().getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
                }).verifyComplete();
        verifyNoInteractions(issuer);
    }

    private void assertPropagation(List<String> capabilities) {
        var knight = new Knight("user-id", "test-user", "test@example.com", Set.of());
        var session = KnightSession.trusted(knight);
        when(sessions.find(session.passport())).thenReturn(Mono.just(session));
        when(issuer.issue(knight)).thenReturn(new Parchment("signed-parchment", Instant.now().plusSeconds(900)));
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/test")
                .cookie(new HttpCookie("KINGDOM_PASSPORT", session.passport()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer incoming-token").build());
        StepVerifier.create(authenticator.authenticate(exchange, tavern(capabilities)))
                .assertNext(result -> {
                    assertEquals(AuthenticationStatus.AUTHENTICATED, result.status());
                    assertEquals(List.of("Bearer signed-parchment"),
                            result.exchange().getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION));
                    assertNull(result.exchange().getResponse().getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
                }).verifyComplete();
        verify(issuer).issue(knight);
    }

    private TavernManifest tavern(List<String> capabilities) {
        return new TavernManifest("test", "test-1", URI.create("http://localhost:8080"),
                "/health", "test", Instant.now(), capabilities, List.of(), List.of());
    }
}
