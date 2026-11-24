package dev.ioza.kingdom.contracts;

import java.net.URI;
import java.time.Instant;
import java.util.List;

public record TavernManifest(String tavernName, String instanceId, URI target, String healthPath,
        String version, Instant startedAt, List<String> capabilities,
        List<TavernRoutePredicate> routePredicates, List<String> publicPaths, String tavernId,
        List<TavernAccess> allowedAccess) {

    public TavernManifest {
        tavernId = tavernId == null || tavernId.isBlank() ? tavernName : tavernId;
        capabilities = capabilities == null ? List.of() : List.copyOf(capabilities);
        routePredicates = routePredicates == null ? List.of() : List.copyOf(routePredicates);
        allowedAccess = allowedAccess == null || allowedAccess.isEmpty()
                ? List.of(TavernAccess.KNIGHT) : List.copyOf(allowedAccess);
    }

    public TavernManifest(String tavernName, String instanceId, URI target,
            String healthPath, String version, Instant startedAt,
            List<String> capabilities, List<TavernRoutePredicate> routePredicates,
            List<String> publicPaths, String tavernId) {
        this(tavernName, instanceId, target, healthPath, version, startedAt,
                capabilities, routePredicates, publicPaths, tavernId, null);
    }

    /** Compatibility with existing Emissaries and persisted manifests. */
    public TavernManifest(String tavernName, String instanceId, URI target,
            String healthPath, String version, Instant startedAt,
            List<String> capabilities, List<TavernRoutePredicate> routePredicates,
            List<String> publicPaths) {
        this(tavernName, instanceId, target, healthPath, version, startedAt,
                capabilities, routePredicates, publicPaths, tavernName, null);
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
