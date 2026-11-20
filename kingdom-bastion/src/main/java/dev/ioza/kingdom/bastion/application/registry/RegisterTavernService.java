package dev.ioza.kingdom.bastion.application.registry;

import dev.ioza.kingdom.bastion.domain.ports.out.TavernRegistryPort;
import dev.ioza.kingdom.contracts.TavernManifest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegisterTavernService {

    private final TavernRegistryPort tavernRegistryPort;
    private final dev.ioza.kingdom.bastion.domain.ports.out.GatewayRoutesPort gatewayRoutesPort;

    public RegisterTavernService(TavernRegistryPort tavernRegistryPort,
            dev.ioza.kingdom.bastion.domain.ports.out.GatewayRoutesPort gatewayRoutesPort) {
        this.tavernRegistryPort = tavernRegistryPort;
        this.gatewayRoutesPort = gatewayRoutesPort;
    }

    public void register(TavernManifest tavern) {
        tavernRegistryPort.save(tavern);
        log.debug("Tavern registered: {}", tavern.instanceId());
        gatewayRoutesPort.refresh();
    }

}
