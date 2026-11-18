package dev.ioza.kingdom.bastion.domain.model.identity;

import java.time.Instant;
import java.util.UUID;

public record KnightSession(

        String passport,

        Knight knight,

        String realmSessionId,

        String accessToken,

        String refreshToken,

        String idToken,

        Instant accessTokenExpiresAt,

        Instant createdAt,

        Instant lastAccessAt,

        AuthenticationSource authSource

) {

    public static KnightSession realm(Knight knight, String realmSessionId, String accessToken,
            String refreshToken, String idToken, Instant accessTokenExpiresAt) {
        return new KnightSession(UUID.randomUUID().toString(), knight, realmSessionId, accessToken,
                refreshToken, idToken, accessTokenExpiresAt, Instant.now(), Instant.now(),
                AuthenticationSource.REALM);
    }

    public static KnightSession trusted(Knight knight) {
        return new KnightSession(UUID.randomUUID().toString(), knight, null, null, null, null, null,
                Instant.now(), Instant.now(), AuthenticationSource.TRUSTED);
    }

    public boolean isRealm() {
        return authSource == AuthenticationSource.REALM;
    }

    public boolean isTrusted() {
        return authSource == AuthenticationSource.TRUSTED;
    }

    public boolean isAccessTokenExpired() {
        return accessTokenExpiresAt != null && Instant.now().isAfter(accessTokenExpiresAt);
    }


}
