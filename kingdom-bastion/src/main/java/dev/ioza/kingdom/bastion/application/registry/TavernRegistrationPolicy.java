package dev.ioza.kingdom.bastion.application.registry;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import dev.ioza.kingdom.bastion.adapters.out.registry.TavernPathRules;
import dev.ioza.kingdom.contracts.TavernManifest;

/**
 * Ownership policy: one logical tavern owns a domain/path and a Bastion context.
 * Multiple instances may register under the same tavernId with identical policy.
 */
public final class TavernRegistrationPolicy {

    public void validate(TavernManifest candidate, Collection<TavernManifest> existing) {
        if (candidate == null || blank(candidate.tavernId()) || blank(candidate.instanceId())
                || candidate.target() == null || candidate.routePredicates() == null
                || candidate.routePredicates().isEmpty()) {
            throw new IllegalArgumentException("Invalid tavern manifest");
        }
        if (!candidate.tavernId().matches("[A-Za-z0-9][A-Za-z0-9._-]{0,127}")) {
            throw new IllegalArgumentException("Invalid tavernId");
        }

        String host = TavernPathRules.domain(candidate).toLowerCase(Locale.ROOT);
        String domainPath = TavernPathRules.domainPath(candidate);
        String context = TavernPathRules.context(candidate);
        if ("bastion.ioza.dev".equals(host)) {
            throw new IllegalArgumentException("Bastion host is reserved");
        }

        for (TavernManifest current : existing) {
            if (current.instanceId().equals(candidate.instanceId())
                    && !current.tavernId().equals(candidate.tavernId())) {
                throw new TavernRegistrationConflictException(
                        "instanceId is already owned by another tavern");
            }

            if (current.tavernId().equals(candidate.tavernId())) {
                if (!host.equalsIgnoreCase(TavernPathRules.domain(current))
                        || !domainPath.equals(TavernPathRules.domainPath(current))
                        || !context.equals(TavernPathRules.context(current))
                        || !Objects.equals(current.capabilities(), candidate.capabilities())
                        || !Objects.equals(current.publicPaths(), candidate.publicPaths())
                        || !Objects.equals(current.allowedAccess(), candidate.allowedAccess())) {
                    throw new TavernRegistrationConflictException(
                            "Instances of the same tavernId must share routing and security policy");
                }
                continue;
            }

            if (host.equalsIgnoreCase(TavernPathRules.domain(current))
                    && overlaps(domainPath, TavernPathRules.domainPath(current))) {
                throw new TavernRegistrationConflictException(
                        "Domain/path is already owned by tavern " + current.tavernId());
            }
            if (overlaps(context, TavernPathRules.context(current))) {
                throw new TavernRegistrationConflictException(
                        "Bastion context overlaps tavern " + current.tavernId());
            }
        }
    }

    private static boolean overlaps(String a, String b) {
        return a.equals(b) || a.startsWith(b + "/") || b.startsWith(a + "/")
                || "/**".equals(a) || "/**".equals(b)
                || (a.endsWith("/**") && (b.startsWith(a.substring(0, a.length() - 3) + "/")
                        || b.equals(a.substring(0, a.length() - 3))))
                || (b.endsWith("/**") && (a.startsWith(b.substring(0, b.length() - 3) + "/")
                        || a.equals(b.substring(0, b.length() - 3))));
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
