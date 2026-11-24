package dev.ioza.kingdom.bastion.adapters.out.registry;

import static org.junit.jupiter.api.Assertions.*;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import dev.ioza.kingdom.contracts.TavernManifest;
import dev.ioza.kingdom.contracts.TavernRoutePredicate;

class TavernDomainContextRoutingTest {
    private TavernManifest tavern(String name, String domain, String domainPath) {
        return new TavernManifest(name, name + "-id", URI.create("http://" + name + ":8080"),
                "/health", "1", null, List.of(), List.of(
                TavernRoutePredicate.domain(domain),
                TavernRoutePredicate.path(domainPath)), List.of());
    }

    @Test void bankFrontDomainAndContext() {
        var tavern = tavern("bank-front", "bank.ioza.dev", "/**");
        var registry = new InMemoryTavernRegistry();
        registry.save(tavern);
        assertEquals("/bank/**", TavernPathRules.contextPath(tavern));
        assertEquals("/assets/index.js", registry.resolve("bank.ioza.dev", "/assets/index.js").orElseThrow().forwardedPath());
        assertEquals("/assets/index.js", registry.resolve("bastion.ioza.dev", "/bank/assets/index.js").orElseThrow().forwardedPath());
        assertTrue(registry.resolve("bastion.ioza.dev", "/").isEmpty());
        assertTrue(registry.resolve("bastion.ioza.dev", "/audience").isEmpty());
    }

    @Test void bankApiKeepsItsNativeApiPath() {
        var tavern = tavern("bank-api", "bank-api.ioza.dev", "/api/v1/**");
        var registry = new InMemoryTavernRegistry();
        registry.save(tavern);
        assertEquals("/bank-api/api/v1/**", TavernPathRules.contextPath(tavern));
        assertTrue(registry.resolve("bank-api.ioza.dev", "/api/v1/households").isPresent());
        assertEquals("/api/v1/households",
                registry.resolve("bastion.ioza.dev", "/bank-api/api/v1/households").orElseThrow().forwardedPath());
        assertTrue(registry.resolve("bastion.ioza.dev", "/bank/api/v1/households").isEmpty());
    }

    @Test void explicitContextOverridesSubdomain() {
        var source = tavern("my-tavern", "service.ioza.dev", "/**");
        var tavern = new TavernManifest(source.tavernName(), source.instanceId(), source.target(),
                source.healthPath(), source.version(), source.startedAt(), source.capabilities(),
                List.of(TavernRoutePredicate.domain("service.ioza.dev"),
                        TavernRoutePredicate.path("/**"),
                        new TavernRoutePredicate("Context", "/custom")), source.publicPaths());
        assertEquals("/custom/**", TavernPathRules.contextPath(tavern));
    }
}
