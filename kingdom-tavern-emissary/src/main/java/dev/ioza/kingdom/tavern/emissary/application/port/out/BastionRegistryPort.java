package dev.ioza.kingdom.tavern.emissary.application.port.out;

import dev.ioza.kingdom.contracts.TavernManifest;

public interface BastionRegistryPort {

    void register(TavernManifest manifest);

    void unregister(String instanceId);

}
