package dev.ioza.kingdom.tavern.emissary.application;

import dev.ioza.kingdom.contracts.TavernManifest;
import dev.ioza.kingdom.tavern.emissary.application.port.out.BastionRegistryPort;


public class EmissaryService {

    private final BastionRegistryPort bastionRegistryPort;

    public EmissaryService(BastionRegistryPort bastionRegistryPort) {
        this.bastionRegistryPort = bastionRegistryPort;
    }

    public void register(TavernManifest manifest) {
        bastionRegistryPort.register(manifest);
    }

    public void unregister(String instanceId) {
        bastionRegistryPort.unregister(instanceId);
    }
}
