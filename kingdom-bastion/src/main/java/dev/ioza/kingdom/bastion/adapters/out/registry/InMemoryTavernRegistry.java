package dev.ioza.kingdom.bastion.adapters.out.registry;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import dev.ioza.kingdom.bastion.domain.ports.out.TavernRegistryPort;
import dev.ioza.kingdom.contracts.TavernManifest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InMemoryTavernRegistry implements TavernRegistryPort {

    private final Map<String, TavernManifest> taverns = new ConcurrentHashMap<>();

    @Override
    public void save(TavernManifest tavern) {
        taverns.put(tavern.instanceId(), tavern);
    }

    @Override
    public Optional<TavernManifest> find(String instanceId) {
        return Optional.ofNullable(taverns.get(instanceId));
    }

    @Override
    public Collection<TavernManifest> findAll() {
        return taverns.values();
    }

    @Override
    public void remove(String instanceId) {
        taverns.remove(instanceId);
    }

    @Override
    public boolean exists(String instanceId) {
        return taverns.containsKey(instanceId);
    }

    @Override
    public long count() {
        return taverns.size();
    }

    @Override
    public Optional<TavernResolution> resolve(String host, String path) {

        String normalizedHost = normalizeHost(host);

        // Dedicated hosts take precedence; context paths belong only to Bastion.
        Optional<TavernManifest> byHost = taverns.values().stream()
                .filter(tavern -> matchesHost(tavern, normalizedHost)).findFirst();

        if (byHost.isPresent()) {
            return Optional.of(new TavernResolution(byHost.get(), path));
        }

        if (!"bastion.ioza.dev".equalsIgnoreCase(normalizedHost)) {
            return Optional.empty();
        }

        return taverns.values().stream().filter(tavern -> matchesPath(tavern, path))
                .sorted((left, right) -> Integer.compare(contextOf(right).length(),
                        contextOf(left).length()))
                .findFirst().map(tavern ->
                        new TavernResolution(tavern, removeContext(path, tavern)));
    }

    private String removeContext(String path, TavernManifest tavern) {

        String context = contextOf(tavern);

        if (path.equals(context)) {
            return "/";
        }

        if (path.startsWith(context + "/")) {
            return path.substring(context.length());
        }

        return path;
    }

    private String contextOf(TavernManifest tavern) {

        return tavern.routePredicates().stream()
                .filter(predicate -> "Path".equalsIgnoreCase(predicate.name())).findFirst()
                .map(predicate -> predicate.value().replace("/**", "")).orElse("");
    }

    private boolean matchesPath(TavernManifest tavern, String path) {

        String context = contextOf(tavern);

        if (context.isBlank()) {
            return false;
        }

        return path.equals(context) || path.startsWith(context + "/");
    }

    private boolean matchesHost(TavernManifest tavern, String host) {

        return tavern.routePredicates().stream()
                .filter(predicate -> "Host".equals(predicate.name())).findFirst()
                .map(predicate -> predicate.value().equalsIgnoreCase(host)).orElse(false);
    }

    private String normalizeHost(String host) {

        int index = host.indexOf(':');

        return index >= 0 ? host.substring(0, index) : host;
    }

}
