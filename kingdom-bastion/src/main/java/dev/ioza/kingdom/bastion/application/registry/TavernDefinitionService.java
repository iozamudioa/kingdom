package dev.ioza.kingdom.bastion.application.registry;

import java.util.Locale;
import org.springframework.stereotype.Service;
import dev.ioza.kingdom.bastion.adapters.out.mongo.repositories.TavernDefinitionMongoRepository;
import dev.ioza.kingdom.bastion.domain.model.registry.TavernDefinition;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TavernDefinitionService {
    private final TavernDefinitionMongoRepository definitions;

    public Mono<TavernDefinition> create(TavernDefinition definition) {
        return definitions.findAll().collectList().flatMap(existing -> {
            for (TavernDefinition other : existing) {
                if (other.tavernId().equals(definition.tavernId()))
                    throw new TavernRegistrationConflictException("tavernId already exists");
                boolean sameDomain = other.domain().equalsIgnoreCase(definition.domain());
                if (sameDomain && overlaps(other.domainPath(), definition.domainPath()))
                    throw new TavernRegistrationConflictException("Domain path already owned by " + other.tavernId());
                if (overlaps(other.context(), definition.context()))
                    throw new TavernRegistrationConflictException("Context already owned by " + other.tavernId());
            }
            return definitions.insert(definition);
        });
    }

    private boolean overlaps(String a, String b) {
        if ("/**".equals(a) || "/**".equals(b)) return true;
        String left = a.endsWith("/**") ? a.substring(0, a.length() - 3) : a;
        String right = b.endsWith("/**") ? b.substring(0, b.length() - 3) : b;
        return left.equals(right) || left.startsWith(right + "/") || right.startsWith(left + "/");
    }
}
