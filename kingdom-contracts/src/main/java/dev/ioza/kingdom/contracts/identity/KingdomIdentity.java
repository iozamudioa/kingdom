package dev.ioza.kingdom.contracts.identity;
import java.util.Objects;
import java.util.Set;

public record KingdomIdentity(String subject, IdentityType type, Set<String> roles, Set<String> decrees) {
    public KingdomIdentity {
        if (subject == null || subject.isBlank()) throw new IllegalArgumentException("Missing subject");
        Objects.requireNonNull(type);
        roles = roles == null ? Set.of() : Set.copyOf(roles);
        decrees = decrees == null ? Set.of() : Set.copyOf(decrees);
    }
    public boolean isKnight() { return type == IdentityType.KNIGHT; }
    public boolean isVassal() { return type == IdentityType.VASSAL; }
    public boolean hasRole(String role) { return roles.contains(role); }
    public boolean hasDecree(String decree) { return decrees.contains(decree); }
}
