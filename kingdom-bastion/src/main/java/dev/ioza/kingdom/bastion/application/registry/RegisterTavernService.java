package dev.ioza.kingdom.bastion.application.registry;

import java.util.Objects;
import java.util.List;
import java.util.UUID;
import dev.ioza.kingdom.bastion.adapters.out.mongo.repositories.TavernDefinitionMongoRepository;
import dev.ioza.kingdom.bastion.domain.model.registry.TavernDefinition;
import dev.ioza.kingdom.bastion.domain.model.registry.TavernInstanceRegistration;
import dev.ioza.kingdom.contracts.TavernRoutePredicate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import dev.ioza.kingdom.bastion.domain.ports.out.TavernRegistryPort;
import dev.ioza.kingdom.bastion.domain.ports.out.TavernPersistencePort;
import dev.ioza.kingdom.bastion.domain.ports.out.GatewayRoutesPort;
import dev.ioza.kingdom.contracts.TavernManifest;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class RegisterTavernService {

    private final TavernRegistryPort registry;
    private final TavernPersistencePort persistence;
    private final GatewayRoutesPort routes;
    private final TavernRegistrationPolicy policy = new TavernRegistrationPolicy();
    private final TavernDefinitionMongoRepository definitions;

    public RegisterTavernService(TavernRegistryPort registry,
            TavernPersistencePort persistence, GatewayRoutesPort routes,
            TavernDefinitionMongoRepository definitions) {
        this.registry = registry;
        this.persistence = persistence;
        this.routes = routes;
        this.definitions = definitions;
    }

    public Mono<Void> register(TavernInstanceRegistration registration) {
        if (registration == null || registration.tavernId() == null
                || registration.instanceId() == null || registration.target() == null
                || !registration.instanceId().startsWith(registration.tavernId() + "-")
                || !isUuid(registration.instanceId().substring(registration.tavernId().length() + 1))) {
            return Mono.error(new IllegalArgumentException("Invalid instance registration"));
        }
        return definitions.findById(registration.tavernId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Tavern is not preconfigured in Bastion")))
                .flatMap(definition -> {
                    if (!definition.enabled()) {
                        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN,
                                "Tavern is disabled"));
                    }
                    TavernManifest manifest = approvedManifest(registration, definition);
                    return persistence.findAll().collectList().flatMap(existing -> {
                        policy.validate(manifest, existing);
                        return persistence.save(manifest);
                    });
                })
                .doOnNext(saved -> {
                    boolean changed = !Objects.equals(registry.find(saved.instanceId()).orElse(null), saved);
                    registry.save(saved);
                    if (changed) routes.refresh();
                    log.debug("Tavern instance registered: {}", saved.instanceId());
                }).then();
    }

    private boolean isUuid(String candidate) {
        try {
            return UUID.fromString(candidate).toString().equalsIgnoreCase(candidate);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private TavernManifest approvedManifest(TavernInstanceRegistration instance,
            TavernDefinition definition) {
        return new TavernManifest(definition.tavernId(), instance.instanceId(),
                instance.target(), instance.healthPath(), instance.version(),
                instance.startedAt(), definition.capabilities(),
                List.of(TavernRoutePredicate.domain(definition.domain()),
                        TavernRoutePredicate.path(definition.domainPath()),
                        new TavernRoutePredicate("Context", definition.context())),
                definition.publicPaths(), definition.tavernId(), definition.allowedAccess());
    }
}
