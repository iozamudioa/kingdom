package dev.ioza.kingdom.contracts;

import java.net.URI;
import java.time.Instant;
import java.util.List;

public record TavernManifest(String tavernName, String instanceId, URI target, String healthPath,
        String version, Instant startedAt, List<String> capabilities,
        List<TavernRoutePredicate> routePredicates, List<String> publicPaths) {

    public TavernManifest {
        capabilities = capabilities == null ? List.of() : List.copyOf(capabilities);
        routePredicates = routePredicates == null ? List.of() : List.copyOf(routePredicates);
    }

    @Override
    public String toString() {
        return "TavernManifest{" + "tavernName='" + tavernName + '\'' + ", instanceId='"
                + instanceId + '\'' + ", target=" + target + ", healthPath='" + healthPath + '\''
                + ", version='" + version + '\'' + ", startedAt=" + startedAt + ", capabilities="
                + capabilities + ", routePredicates=" + routePredicates + ", publicPaths="
                + publicPaths + '}';
    }



}
