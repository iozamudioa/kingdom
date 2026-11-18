package dev.ioza.kingdom.bastion.domain.model.identity;

import java.util.Set;

public record Vassal(String id, String name, Set<String> roles) {

    public Vassal {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Vassal id cannot be null or blank");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Vassal name cannot be null or blank");
        }

        roles = roles == null ? Set.of() : Set.copyOf(roles);

    }

}
