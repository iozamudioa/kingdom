package dev.ioza.kingdom.bastion.application.registry;

import static org.junit.jupiter.api.Assertions.*;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import dev.ioza.kingdom.contracts.TavernManifest;
import dev.ioza.kingdom.contracts.TavernRoutePredicate;

class TavernRegistrationPolicyTest {
    private final TavernRegistrationPolicy policy = new TavernRegistrationPolicy();

    private TavernManifest manifest(String id, String instance, String domain,
            String path, String context) {
        return new TavernManifest(id, instance, URI.create("http://" + instance + ":8080"),
                "/health", "1", null, List.of("ui"),
                List.of(TavernRoutePredicate.domain(domain), TavernRoutePredicate.path(path),
                        new TavernRoutePredicate("Context", context)), List.of("/assets/**"), id);
    }

    @Test void multipleInstancesCanShareOneLogicalTavern() {
        var a = manifest("bank", "bank-01", "bank.ioza.dev", "/**", "/bank");
        var b = manifest("bank", "bank-02", "bank.ioza.dev", "/**", "/bank");
        assertDoesNotThrow(() -> policy.validate(b, List.of(a)));
    }

    @Test void differentTavernCannotClaimDomain() {
        var a = manifest("bank", "bank-01", "bank.ioza.dev", "/**", "/bank");
        var b = manifest("evil", "evil-01", "bank.ioza.dev", "/**", "/evil");
        assertThrows(TavernRegistrationConflictException.class,
                () -> policy.validate(b, List.of(a)));
    }

    @Test void differentTavernCannotClaimOverlappingContext() {
        var a = manifest("bank", "bank-01", "bank.ioza.dev", "/**", "/bank");
        var b = manifest("other", "other-01", "other.ioza.dev", "/**", "/bank/admin");
        assertThrows(TavernRegistrationConflictException.class,
                () -> policy.validate(b, List.of(a)));
    }

    @Test void sameTavernCannotChangePolicyWithNewInstance() {
        var a = manifest("bank", "bank-01", "bank.ioza.dev", "/**", "/bank");
        var b = manifest("bank", "bank-02", "bank.ioza.dev", "/private/**", "/bank");
        assertThrows(TavernRegistrationConflictException.class,
                () -> policy.validate(b, List.of(a)));
    }

    @Test void cannotReuseInstanceIdAcrossTaverns() {
        var a = manifest("bank", "shared", "bank.ioza.dev", "/**", "/bank");
        var b = manifest("other", "shared", "other.ioza.dev", "/**", "/other");
        assertThrows(TavernRegistrationConflictException.class,
                () -> policy.validate(b, List.of(a)));
    }
}
