package dev.ioza.kingdom.bastion.adapters.out.registry;

import static org.junit.jupiter.api.Assertions.*;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import dev.ioza.kingdom.contracts.TavernManifest;
import dev.ioza.kingdom.contracts.TavernRoutePredicate;

class InMemoryTavernRegistryTest {

    private TavernManifest tavern(String name, String host, String context) {
        return new TavernManifest(name, name + "-1", URI.create("http://" + name + ":8080"),
                "/health", "1", null, List.of(),
                List.of(TavernRoutePredicate.domain(host), TavernRoutePredicate.path("/**")),
                List.of());
    }

    @Test
    void domainAndContextResolveToSameForwardedPath() {
        var registry = new InMemoryTavernRegistry();
        registry.save(tavern("cctv", "cctv.ioza.dev", "/cctv"));
        var byDomain = registry.resolve("cctv.ioza.dev", "/api/flights/latest").orElseThrow();
        var byContext = registry.resolve("bastion.ioza.dev", "/cctv/api/flights/latest").orElseThrow();
        assertEquals(byDomain.tavern().instanceId(), byContext.tavern().instanceId());
        assertEquals("/api/flights/latest", byDomain.forwardedPath());
        assertEquals(byDomain.forwardedPath(), byContext.forwardedPath());
    }

    @Test
    void domainCannotResolveAnotherTavernByContext() {
        var registry = new InMemoryTavernRegistry();
        registry.save(tavern("tower", "tower.ioza.dev", "/tower"));
        registry.save(tavern("cctv", "cctv.ioza.dev", "/cctv"));
        assertEquals("tower", registry.resolve("tower.ioza.dev", "/cctv/api").orElseThrow().tavern().tavernName());
        assertTrue(registry.resolve("unknown.ioza.dev", "/cctv/api").isEmpty());
    }

    @Test
    void contextRootIsForwardedAsSlash() {
        var registry = new InMemoryTavernRegistry();
        registry.save(tavern("tower", "tower.ioza.dev", "/tower"));
        assertEquals("/", registry.resolve("bastion.ioza.dev", "/tower").orElseThrow().forwardedPath());
    }
}
