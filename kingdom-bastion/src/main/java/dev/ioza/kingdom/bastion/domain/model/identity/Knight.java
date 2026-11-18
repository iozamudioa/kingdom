package dev.ioza.kingdom.bastion.domain.model.identity;

import java.util.Set;

public record Knight(String id, String username, String email, Set<String> roles) {

    public static Knight trusted(String id) {
        return new Knight(id, "Trusted", "trusted@device.com", Set.of());
    }

}
