package dev.ioza.kingdom.bastion.adapters.out.gateway;

import java.util.List;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import dev.ioza.kingdom.bastion.adapters.out.registry.TavernPathRules;
import dev.ioza.kingdom.bastion.domain.ports.out.TavernRegistryPort;
import dev.ioza.kingdom.contracts.TavernManifest;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class TavernRouteDefinitionRepository implements RouteDefinitionRepository {
    private final TavernRegistryPort registry;

    public TavernRouteDefinitionRepository(TavernRegistryPort registry) {
        this.registry = registry;
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        // One pair of Gateway routes per logical tavern, regardless of replicas.
        Map<String, TavernManifest> representatives = new TreeMap<>();
        registry.findAll().stream()
                .sorted(Comparator.comparing(TavernManifest::instanceId))
                .forEach(t -> representatives.putIfAbsent(t.tavernId(), t));
        return Flux.fromIterable(representatives.values()).flatMapIterable(this::routes);
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> routeDefinition) {
        return Mono.error(new UnsupportedOperationException("Gateway routes are managed by the Tavern Registry"));
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return Mono.error(new UnsupportedOperationException("Gateway routes are managed by the Tavern Registry"));
    }

    private Iterable<RouteDefinition> routes(TavernManifest tavern) {
        return List.of(route(tavern, false), route(tavern, true));
    }

    private RouteDefinition route(TavernManifest tavern, boolean context) {
        String host = context ? "bastion.ioza.dev" : TavernPathRules.domain(tavern);
        String path = context ? TavernPathRules.contextPath(tavern) : TavernPathRules.domainPath(tavern);
        RouteDefinition definition = new RouteDefinition();
        definition.setId(tavern.tavernId() + (context ? "-context" : "-domain"));
        definition.setUri(tavern.target());
        definition.setPredicates(List.of(predicate("Host", host), predicate("Path", path)));
        definition.setOrder(-path.length());
        log.info("Registered {} route '{}' [{} {}] -> {}",
                context ? "CONTEXT" : "DOMAIN", definition.getId(), host, path, definition.getUri());
        return definition;
    }

    private PredicateDefinition predicate(String name, String value) {
        PredicateDefinition definition = new PredicateDefinition();
        definition.setName(name);
        definition.addArg("pattern", value);
        return definition;
    }
}
