package dev.ioza.kingdom.bastion.domain.model.parchment;

import java.time.Instant;
import java.util.Set;

public record ParchmentClaims(

        String subject,

        String identityType,

        Set<String> roles,

        Set<String> decrees,

        Instant issuedAt,

        Instant expiresAt

) {

    public ParchmentClaims {

        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("ParchmentClaims subject cannot be null or blank");
        }

        if (identityType == null || identityType.isBlank()) {
            throw new IllegalArgumentException(
                    "ParchmentClaims identityType cannot be null or blank");
        }

        roles = roles == null ? Set.of() : Set.copyOf(roles);
        decrees = decrees == null ? Set.of() : Set.copyOf(decrees);

        if (issuedAt == null) {
            throw new IllegalArgumentException("ParchmentClaims issuedAt cannot be null");
        }

        if (expiresAt == null) {
            throw new IllegalArgumentException("ParchmentClaims expiresAt cannot be null");
        }

        if (!expiresAt.isAfter(issuedAt)) {
            throw new IllegalArgumentException("ParchmentClaims expiresAt must be after issuedAt");
        }

    }
}
