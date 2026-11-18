package dev.ioza.kingdom.bastion.domain.ports.out;

import java.util.Collection;
import java.util.Optional;
import dev.ioza.kingdom.bastion.adapters.out.registry.TavernResolution;
import dev.ioza.kingdom.contracts.TavernManifest;

public interface TavernRegistryPort {

    void save(TavernManifest tavern);

    Optional<TavernManifest> find(String instanceId);

    Collection<TavernManifest> findAll();

    void remove(String instanceId);

    boolean exists(String instanceId);

    long count();

    Optional<TavernResolution> resolve(String host, String path);

}
