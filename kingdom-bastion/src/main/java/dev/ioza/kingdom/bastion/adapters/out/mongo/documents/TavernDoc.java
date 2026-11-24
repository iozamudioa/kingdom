package dev.ioza.kingdom.bastion.adapters.out.mongo.documents;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import dev.ioza.kingdom.contracts.TavernManifest;

@Document("kingdom_taverns")
public record TavernDoc(
        @Id String instanceId,
        TavernManifest manifest,
        Instant registeredAt,
        Instant lastSeenAt) {
}
