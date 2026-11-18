package dev.ioza.kingdom.bastion.adapters.out.realm;

import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import dev.ioza.kingdom.bastion.domain.model.realm.InvalidRealmLogoutTokenException;
import dev.ioza.kingdom.bastion.domain.model.realm.RealmLogout;
import dev.ioza.kingdom.bastion.domain.ports.out.RealmLogoutTokenPort;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@Component
public class KeycloakLogoutTokenAdapter implements RealmLogoutTokenPort {

    private static final String LOGOUT_TOKEN_TYPE = "logout+jwt";

    private static final String BACKCHANNEL_LOGOUT_EVENT =
            "http://schemas.openid.net/event/backchannel-logout";

    private final ReactiveJwtDecoder logoutJwtDecoder;
    private final KeycloakRealmProperties properties;

    public KeycloakLogoutTokenAdapter(
            @Qualifier("realmLogoutJwtDecoder") ReactiveJwtDecoder logoutJwtDecoder,
            KeycloakRealmProperties properties) {

        this.logoutJwtDecoder = logoutJwtDecoder;
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        System.out.println("KeycloakLogoutTokenAdapter initialized with properties: " + properties);
    }

    @Override
    public Mono<RealmLogout> validate(String logoutToken) {

        return logoutJwtDecoder.decode(logoutToken).map(this::validateClaims)
                .map(jwt -> new RealmLogout(jwt.getSubject(), jwt.getClaimAsString("sid")));
    }

    private Jwt validateClaims(Jwt jwt) {

        validateType(jwt);
        validateAudience(jwt);
        validateEvents(jwt);

        if (jwt.hasClaim("nonce")) {
            throw new InvalidRealmLogoutTokenException("Logout token must not contain nonce");
        }

        String sid = jwt.getClaimAsString("sid");
        String subject = jwt.getSubject();

        if (isBlank(sid) && isBlank(subject)) {
            throw new InvalidRealmLogoutTokenException("Logout token must contain sid or sub");
        }

        return jwt;
    }

    private void validateType(Jwt jwt) {

        Object type = jwt.getHeaders().get("typ");

        if (!LOGOUT_TOKEN_TYPE.equals(type)) {
            throw new InvalidRealmLogoutTokenException("Logout token typ must be logout+jwt");
        }
    }

    private void validateAudience(Jwt jwt) {

        String clientId = properties.herald().clientId();

        if (jwt.getAudience() == null || !jwt.getAudience().contains(clientId)) {

            throw new InvalidRealmLogoutTokenException(
                    "Logout token audience does not contain " + clientId);
        }
    }

    private void validateEvents(Jwt jwt) {

        Map<String, Object> events = jwt.getClaim("events");

        if (events == null || !events.containsKey(BACKCHANNEL_LOGOUT_EVENT)) {

            throw new InvalidRealmLogoutTokenException("Invalid backchannel logout event");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
