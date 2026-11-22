package dev.ioza.kingdom.tavern.lock.infrastructure.security;
import java.util.Optional;
import java.util.List;
import java.util.Set;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import dev.ioza.kingdom.contracts.identity.*;

public final class SpringSecurityIdentityProvider implements KingdomIdentityProvider {
    @Override
    public Optional<KingdomIdentity> current() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken token) || !token.isAuthenticated()) {
            return Optional.empty();
        }
        var jwt = token.getToken();
        String type = jwt.getClaimAsString("identity_type");
        if (!"KNIGHT".equals(type) && !"VASSAL".equals(type)) return Optional.empty();
        if (jwt.getSubject() == null || jwt.getSubject().isBlank()) return Optional.empty();
        return Optional.of(new KingdomIdentity(jwt.getSubject(), IdentityType.valueOf(type),
                asSet(jwt.getClaimAsStringList("roles")), asSet(jwt.getClaimAsStringList("decrees"))));
    }
    private static Set<String> asSet(List<String> values) {
        return values == null ? Set.of() : Set.copyOf(values);
    }
}
