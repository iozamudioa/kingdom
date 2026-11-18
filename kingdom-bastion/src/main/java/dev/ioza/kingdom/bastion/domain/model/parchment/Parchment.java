package dev.ioza.kingdom.bastion.domain.model.parchment;

import java.time.Instant;

public record Parchment(

        String value,

        Instant expiresAt) {

    public Parchment {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Parchment value cannot be null or blank");
        }

        if (expiresAt == null) {
            throw new IllegalArgumentException("Parchment expiresAt cannot be null");
        }

    }

}
