package dev.ioza.kingdom.bastion.adapters.out.realm;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Set;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import dev.ioza.kingdom.bastion.domain.model.identity.Knight;
import dev.ioza.kingdom.bastion.domain.model.identity.Vassal;
import dev.ioza.kingdom.bastion.domain.model.realm.InvalidRealmCredentialsException;
import dev.ioza.kingdom.bastion.domain.model.realm.RealmAuthenticationException;
import dev.ioza.kingdom.bastion.domain.model.realm.RealmCredentials;
import dev.ioza.kingdom.bastion.domain.model.realm.RealmKnightAuthentication;
import dev.ioza.kingdom.bastion.domain.model.realm.RealmUnavailableException;
import dev.ioza.kingdom.bastion.domain.ports.out.RealmPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public class KeycloakRealmAdapter implements RealmPort {

        private final WebClient webClient;
        private final KeycloakRealmProperties properties;
        private final ReactiveJwtDecoder realmJwtDecoder;

        @Override
        public Mono<Vassal> authenticateVassal(RealmCredentials credentials) {
                return verifyCredentials(credentials).thenReturn(
                                new Vassal(credentials.id(), credentials.id(), Set.of()));
        }

        private Mono<Void> verifyCredentials(RealmCredentials credentials) {

                LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();

                form.add("grant_type", "client_credentials");
                form.add("client_id", credentials.id());
                form.add("client_secret", credentials.clientSecret());

                return webClient.post().uri(properties.tokenUri())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED).bodyValue(form)
                                .retrieve()

                                .onStatus(status -> status.value() == 400 || status.value() == 401,
                                                response -> Mono.error(
                                                                new InvalidRealmCredentialsException()))

                                .onStatus(HttpStatusCode::is5xxServerError, response -> Mono
                                                .error(new RealmUnavailableException(
                                                                "Status " + response.statusCode())))

                                .toBodilessEntity()

                                .then()

                                .onErrorMap(WebClientRequestException.class,
                                                ex -> new RealmUnavailableException(
                                                                "Could not communicate with Keycloak"));
        }



        @Override
        public Mono<URI> beginAudience(String state) {

                String url = properties.authorizationUri() + "?client_id="
                                + properties.herald().clientId() + "&response_type=code"
                                + "&scope=openid" + "&redirect_uri="
                                + URLEncoder.encode(properties.herald().callbackUri(),
                                                StandardCharsets.UTF_8)
                                + "&state=" + state;

                return Mono.just(URI.create(url));

        }

        @Override
        public Mono<RealmKnightAuthentication> authenticateKnight(String authorizationCode) {
                return authenticate(exchangeAuthorizationCode(authorizationCode));
        }

        @Override
        public Mono<Knight> authenticateKnight(String username, String password) {
                var client = properties.knightSeal();
                if (client == null || client.clientId() == null || client.clientId().isBlank()
                        || client.clientSecret() == null || client.clientSecret().isBlank()) {
                        return Mono.error(new RealmUnavailableException("Knight seal client is not configured"));
                }
                MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
                form.add("grant_type", "password");
                form.add("scope", "openid");
                form.add("client_id", client.clientId());
                form.add("client_secret", client.clientSecret());
                form.add("username", username);
                form.add("password", password);
                return webClient.post().uri(properties.tokenUri())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED).bodyValue(form)
                        .retrieve()
                        .onStatus(status -> status.value() == 400 || status.value() == 401,
                                response -> Mono.error(new InvalidRealmCredentialsException()))
                        .onStatus(HttpStatusCode::isError,
                                response -> Mono.error(new RealmUnavailableException("Knight authentication unavailable")))
                        .bodyToMono(KeycloakTokenResponse.class)
                        .flatMap(response -> {
                                if (response.idToken() == null || response.idToken().isBlank()) {
                                        return Mono.error(new RealmUnavailableException("Missing identity token"));
                                }
                                return realmJwtDecoder.decode(response.idToken());
                        })
                        .filter(jwt -> jwt.getAudience() != null && jwt.getAudience().contains(client.clientId())
                                && jwt.getClaimAsString("preferred_username") != null
                                && !jwt.getClaimAsString("preferred_username").isBlank())
                        .switchIfEmpty(Mono.error(new RealmUnavailableException("Invalid realm identity")))
                        .map(this::toKnight)
                        .onErrorMap(org.springframework.security.oauth2.jwt.JwtException.class,
                                ex -> new RealmUnavailableException("Invalid realm identity"))
                        .onErrorMap(WebClientRequestException.class,
                                ex -> new RealmUnavailableException("Could not communicate with Keycloak"));
        }

        private RealmKnightAuthentication toRealmKnightAuthentication(
                        KeycloakTokenResponse response, Jwt idToken) {

                return new RealmKnightAuthentication(toKnight(idToken),
                                idToken.getClaimAsString("sid"), response.accessToken(),
                                response.refreshToken(), response.idToken(),
                                Instant.now().plusSeconds(response.expiresIn()));

        }

        private Mono<RealmKnightAuthentication> authenticate(Mono<KeycloakTokenResponse> exchange) {
                return exchange.flatMap(response -> realmJwtDecoder.decode(response.idToken())
                                .map(jwt -> toRealmKnightAuthentication(response, jwt)));
        }

        private Mono<KeycloakTokenResponse> exchangeAuthorizationCode(String authorizationCode) {

                MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

                form.add("grant_type", "authorization_code");
                form.add("client_id", properties.herald().clientId());
                form.add("client_secret", properties.herald().clientSecret());
                form.add("code", authorizationCode);
                form.add("redirect_uri", properties.herald().callbackUri());

                return exchange(form);
        }

        private Knight toKnight(Jwt jwt) {
                return new Knight(jwt.getSubject(), jwt.getClaimAsString("preferred_username"),
                                jwt.getClaimAsString("email"), Set.of());
        }

        @Override
        public Mono<RealmKnightAuthentication> refreshKnight(String refreshToken) {

                MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

                form.add("grant_type", "refresh_token");
                form.add("client_id", properties.herald().clientId());
                form.add("client_secret", properties.herald().clientSecret());
                form.add("refresh_token", refreshToken);

                return authenticate(exchange(form));
        }


        private Mono<KeycloakTokenResponse> exchange(MultiValueMap<String, String> form) {
                return webClient.post().uri(properties.tokenUri())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED).bodyValue(form)
                                .retrieve()
                                .onStatus(HttpStatusCode::isError, response -> response
                                                .bodyToMono(String.class)
                                                .flatMap(body -> Mono.<Throwable>error(
                                                                new RealmAuthenticationException(
                                                                                body))))
                                .bodyToMono(KeycloakTokenResponse.class);
        }

        @Override
        public Mono<Void> logout(String refreshToken) {
                MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

                form.add("client_id", properties.herald().clientId());
                form.add("client_secret", properties.herald().clientSecret());
                form.add("refresh_token", refreshToken);

                return postForm(properties.logoutUri(), form);
        }

        private Mono<Void> postForm(String uri, MultiValueMap<String, String> form) {

                return webClient.post().uri(uri).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .bodyValue(form).retrieve()

                                .onStatus(HttpStatusCode::isError, response -> response
                                                .bodyToMono(String.class)
                                                .flatMap(body -> Mono.error(
                                                                new RealmAuthenticationException(
                                                                                body))))

                                .toBodilessEntity().then()

                                .onErrorMap(WebClientRequestException.class,
                                                ex -> new RealmUnavailableException(
                                                                "Could not communicate with Keycloak"));
        }

}
