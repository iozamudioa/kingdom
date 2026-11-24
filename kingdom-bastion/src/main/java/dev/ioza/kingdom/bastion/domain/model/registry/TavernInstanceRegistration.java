package dev.ioza.kingdom.bastion.domain.model.registry;

import java.net.URI;
import java.time.Instant;

public record TavernInstanceRegistration(
        String tavernId, String instanceId, URI target,
        String healthPath, String version, Instant startedAt) {
}
