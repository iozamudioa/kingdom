package dev.ioza.kingdom.contracts.identity;
import java.util.Optional;
public interface KingdomIdentityProvider {
    Optional<KingdomIdentity> current();
    default KingdomIdentity require() { return current().orElseThrow(IdentityNotFoundException::new); }
    default String subject() { return require().subject(); }
}
