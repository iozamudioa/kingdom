package dev.ioza.kingdom.bastion.adapters.out.mongo.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import dev.ioza.kingdom.bastion.domain.model.registry.TavernDefinition;

public interface TavernDefinitionMongoRepository
        extends ReactiveMongoRepository<TavernDefinition, String> {
}
