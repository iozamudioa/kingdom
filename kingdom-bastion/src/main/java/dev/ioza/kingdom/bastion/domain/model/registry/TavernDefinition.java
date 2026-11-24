package dev.ioza.kingdom.bastion.domain.model.registry;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import dev.ioza.kingdom.contracts.TavernAccess;

/** Bastion-owned security and routing configuration, not supplied by replicas. */
@Document("kingdom_tavern_definitions")
public record TavernDefinition(
        @Id String tavernId,
        String domain,
        String domainPath,
        String context,
        List<TavernAccess> allowedAccess,
        List<String> capabilities,
        List<String> publicPaths,
        boolean enabled) {

    public TavernDefinition {
        if (tavernId == null || !tavernId.matches("[A-Za-z0-9][A-Za-z0-9._-]{0,127}"))
            throw new IllegalArgumentException("Invalid tavernId");
        if (domain == null || !domain.matches("(?i)[a-z0-9-]+(?:\\.[a-z0-9-]+)+"))
            throw new IllegalArgumentException("Invalid tavern domain");
        if (domain.equalsIgnoreCase("bastion.ioza.dev"))
            throw new IllegalArgumentException("Bastion host is reserved");
        domainPath = domainPath == null || domainPath.isBlank() ? "/**" : domainPath;
        if (!domainPath.startsWith("/") || (!domainPath.endsWith("/**") && !domainPath.equals("/")))
            throw new IllegalArgumentException("Invalid domainPath");
        if (context == null || !context.matches("/[A-Za-z0-9_-]+(?:/[A-Za-z0-9_-]+)*"))
            throw new IllegalArgumentException("Invalid context");
        allowedAccess = allowedAccess == null || allowedAccess.isEmpty()
                ? List.of(TavernAccess.KNIGHT) : List.copyOf(allowedAccess);
        capabilities = capabilities == null ? List.of() : List.copyOf(capabilities);
        publicPaths = publicPaths == null ? List.of() : List.copyOf(publicPaths);
    }
}
