package dev.ioza.kingdom.bastion.adapters.out.gateway;

import static org.junit.jupiter.api.Assertions.*;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import dev.ioza.kingdom.bastion.adapters.out.registry.InMemoryTavernRegistry;
import dev.ioza.kingdom.contracts.TavernManifest;
import dev.ioza.kingdom.contracts.TavernRoutePredicate;

class TavernRouteDefinitionRepositoryTest {
    private TavernManifest instance(String id, String target) {
        return new TavernManifest("Bank", id, URI.create(target), "/health", "1", null,
                List.of("ui"),
                List.of(TavernRoutePredicate.domain("bank.ioza.dev"),
                        TavernRoutePredicate.path("/**"),
                        new TavernRoutePredicate("Context", "/bank")),
                List.of("/assets/**"), "bank");
    }

    @Test
    void twoInstancesCreateOnlyOneDomainAndOneContextRoute() {
        var registry = new InMemoryTavernRegistry();
        registry.save(instance("bank-1", "http://bank-1:80"));
        registry.save(instance("bank-2", "http://bank-2:80"));
        var routes = new TavernRouteDefinitionRepository(registry).getRouteDefinitions()
                .collectList().block();
        assertNotNull(routes);
        assertEquals(2, routes.size());
        assertTrue(routes.stream().anyMatch(r -> "bank-domain".equals(r.getId())));
        assertTrue(routes.stream().anyMatch(r -> "bank-context".equals(r.getId())));
        assertTrue(routes.stream().anyMatch(r -> r.getPredicates().toString().contains("/bank/**")));
    }
}
