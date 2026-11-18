package dev.ioza.kingdom.bastion.adapters.out.gateway;

import java.util.List;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import dev.ioza.kingdom.bastion.domain.ports.out.TavernRegistryPort;
import dev.ioza.kingdom.contracts.TavernManifest;
import dev.ioza.kingdom.contracts.TavernRoutePredicate;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class TavernRouteDefinitionRepository implements RouteDefinitionRepository {

    private static final String HOST = "Host";
    private static final String PATH = "Path";

    private final TavernRegistryPort tavernRegistryPort;

    public TavernRouteDefinitionRepository(TavernRegistryPort tavernRegistryPort) {
        this.tavernRegistryPort = tavernRegistryPort;
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {

        return Flux.fromIterable(tavernRegistryPort.findAll())
                .doOnNext(t -> log.info("Building routes for {}", t.tavernName()))
                .flatMapIterable(this::createRouteDefinitions)
                .doOnNext(r -> log.info("Route: {} {}", r.getId(), r.getPredicates()));

    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> routeDefinition) {
        return Mono.error(new UnsupportedOperationException(
                "Gateway routes are managed by the Tavern Registry"));
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return Mono.error(new UnsupportedOperationException(
                "Gateway routes are managed by the Tavern Registry"));
    }

    private Iterable<RouteDefinition> createRouteDefinitions(TavernManifest tavern) {

        return List.of(createDomainRoute(tavern), createContextRoute(tavern));

    }

    private RouteDefinition createDomainRoute(TavernManifest tavern) {

        RouteDefinition definition = new RouteDefinition();

        definition.setId(tavern.instanceId() + "-domain");
        definition.setUri(tavern.target());

        definition.setPredicates(List.of(predicate(HOST, domain(tavern)), predicate(PATH, "/**")));

        log.info("Registered DOMAIN route '{}' [{} {}] -> {}", definition.getId(), domain(tavern),
                "/**", definition.getUri());

        return definition;
    }

    private RouteDefinition createContextRoute(TavernManifest tavern) {

        RouteDefinition definition = new RouteDefinition();

        definition.setId(tavern.instanceId() + "-context");
        definition.setUri(tavern.target());

        definition.setPredicates(
                List.of(predicate(HOST, "bastion.ioza.dev"), predicate(PATH, path(tavern))));

        definition.getFilters().add(stripPrefixFilter());

        log.info("Registered CONTEXT route '{}' [{} {}] -> {}", definition.getId(),
                "bastion.ioza.dev", path(tavern), definition.getUri());

        return definition;
    }

    private PredicateDefinition predicate(String name, String value) {

        PredicateDefinition definition = new PredicateDefinition();

        definition.setName(name);
        definition.addArg("pattern", value);

        return definition;
    }

    private FilterDefinition stripPrefixFilter() {

        FilterDefinition filter = new FilterDefinition();

        filter.setName("StripPrefix");
        filter.addArg("parts", "1");

        return filter;
    }

    private String domain(TavernManifest tavern) {

        return tavern.routePredicates().stream()
                .filter(predicate -> HOST.equalsIgnoreCase(predicate.name())).findFirst()
                .map(TavernRoutePredicate::value).orElseThrow();

    }

    private String path(TavernManifest tavern) {

        return tavern.routePredicates().stream()
                .filter(predicate -> PATH.equalsIgnoreCase(predicate.name())).findFirst()
                .map(TavernRoutePredicate::value).orElseThrow();

    }



}
