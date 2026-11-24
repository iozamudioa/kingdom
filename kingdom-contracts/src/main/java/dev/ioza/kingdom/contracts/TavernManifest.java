package dev.ioza.kingdom.contracts;

import java.net.URI;
import java.time.Instant;
import java.util.List;

public record TavernManifest(String tavernName, String instanceId, URI target, String healthPath,
        String version, Instant startedAt, List<String> capabilities,
        List<TavernRoutePredicate> routePredicates, List<String> publicPaths, String tavernId) {

    public TavernManifest {
        tavernId = tavernId == null || tavernId.isBlank() ? tavernName : tavernId;
        capabilities = capabilities == null ? List.of() : List.copyOf(capabilities);
        routePredicates = routePredicates == null ? List.of() : List.copyOf(routePredicates);
    }

    /** Compatibility with existing Emissaries and persisted manifests. */
    public TavernManifest(String tavernName, String instanceId, URI target,
            String healthPath, String version, Instant startedAt,
            List<String> capabilities, List<TavernRoutePredicate> routePredicates,
            List<String> publicPaths) {
        this(tavernName, instanceId, target, healthPath, version, startedAt,
                capabilities, routePredicates, publicPaths, tavernName);
    }

    @Override
    public String toString() {
        return "TavernManifest{" + "tavernName='" + tavernName + '\'' + ", instanceId='"
                + instanceId + '\'' + ", tavernId=" + tavernId + ", target=" + target + ", healthPath='" + healthPath + '\''
                + ", version='" + version + '\'' + ", startedAt=" + startedAt + ", capabilities="
                + capabilities + ", routePredicates=" + routePredicates + ", publicPaths="
                + publicPaths + '}';
    }



}
