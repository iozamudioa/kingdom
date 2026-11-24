package dev.ioza.kingdom.bastion.adapters.out.registry;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import dev.ioza.kingdom.bastion.domain.ports.out.TavernRegistryPort;
import dev.ioza.kingdom.contracts.TavernManifest;

public class InMemoryTavernRegistry implements TavernRegistryPort {
    private final Map<String, TavernManifest> taverns = new ConcurrentHashMap<>();

    @Override public void save(TavernManifest tavern) { taverns.put(tavern.instanceId(), tavern); }
    @Override public Optional<TavernManifest> find(String instanceId) { return Optional.ofNullable(taverns.get(instanceId)); }
    @Override public Collection<TavernManifest> findAll() { return taverns.values(); }
    @Override public void remove(String instanceId) { taverns.remove(instanceId); }
    @Override public boolean exists(String instanceId) { return taverns.containsKey(instanceId); }
    @Override public long count() { return taverns.size(); }

    @Override
    public Optional<TavernResolution> resolve(String host, String path) {
        String normalizedHost = normalizeHost(host);
        if ("bastion.ioza.dev".equalsIgnoreCase(normalizedHost)) {
            return taverns.values().stream()
                    .filter(tavern -> TavernPathRules.matchesContext(tavern, path))
                    .max(Comparator.comparingInt(tavern -> TavernPathRules.context(tavern).length()))
                    .map(tavern -> new TavernResolution(tavern, TavernPathRules.removeContext(path, tavern)));
        }
        return taverns.values().stream()
                .filter(tavern -> TavernPathRules.domain(tavern).equalsIgnoreCase(normalizedHost)
                        && TavernPathRules.matchesPattern(TavernPathRules.domainPath(tavern), path))
                .max(Comparator.comparingInt(tavern -> TavernPathRules.domainPath(tavern).length()))
                .map(tavern -> new TavernResolution(tavern, path));
    }

    private String normalizeHost(String host) {
        if (host == null) return "";
        int index = host.indexOf(':');
        return index >= 0 ? host.substring(0, index) : host;
    }
}
