package dev.ioza.kingdom.bastion.adapters.out.mongo.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import dev.ioza.kingdom.bastion.adapters.out.mongo.documents.TavernDoc;

public interface TavernMongoRepository extends ReactiveMongoRepository<TavernDoc, String> {
}
