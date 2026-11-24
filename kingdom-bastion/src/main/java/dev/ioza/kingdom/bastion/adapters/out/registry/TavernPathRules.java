package dev.ioza.kingdom.bastion.adapters.out.registry;

import java.util.Locale;
import dev.ioza.kingdom.contracts.TavernManifest;
import dev.ioza.kingdom.contracts.TavernRoutePredicate;

/** Separates a tavern's domain path from its Bastion-mounted context. */
public final class TavernPathRules {
    private TavernPathRules() {}

    public static String domain(TavernManifest tavern) {
        return tavern.routePredicates().stream()
                .filter(p -> "Host".equalsIgnoreCase(p.name()))
                .map(TavernRoutePredicate::value).findFirst().orElseThrow();
    }

    public static String domainPath(TavernManifest tavern) {
        return tavern.routePredicates().stream()
                .filter(p -> "Path".equalsIgnoreCase(p.name()))
                .map(TavernRoutePredicate::value).findFirst().orElseThrow();
    }

    public static String context(TavernManifest tavern) {
        String configured = tavern.routePredicates().stream()
                .filter(p -> "Context".equalsIgnoreCase(p.name()))
                .map(TavernRoutePredicate::value).findFirst().orElse(null);
        String value = configured != null ? configured : "/" + domain(tavern).split("\\.")[0].toLowerCase(Locale.ROOT);
        if (!value.matches("/[a-zA-Z0-9_-]+(?:/[a-zA-Z0-9_-]+)*")) {
            throw new IllegalArgumentException("Invalid tavern context: " + value);
        }
        return value;
    }

    public static String contextPath(TavernManifest tavern) {
        String pattern = domainPath(tavern);
        return context(tavern) + ("/**".equals(pattern) ? "/**" : pattern);
    }

    public static boolean matchesPattern(String pattern, String path) {
        if ("/**".equals(pattern)) return path.startsWith("/");
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return path.equals(prefix) || path.startsWith(prefix + "/");
        }
        return path.equals(pattern);
    }

    public static String removeContext(String path, TavernManifest tavern) {
        String context = context(tavern);
        if (path.equals(context)) return "/";
        if (path.startsWith(context + "/")) return path.substring(context.length());
        return path;
    }

    public static boolean matchesContext(TavernManifest tavern, String path) {
        String context = context(tavern);
        if (!path.equals(context) && !path.startsWith(context + "/")) return false;
        return matchesPattern(domainPath(tavern), removeContext(path, tavern));
    }
}
