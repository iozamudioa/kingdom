package dev.ioza.kingdom.bastion.domain.model.realm;

import java.time.Instant;
import dev.ioza.kingdom.bastion.domain.model.identity.Knight;

public record RealmKnightAuthentication(

                Knight knight,

                String realmSessionId,

                String accessToken,

                String refreshToken,

                String idToken,

                Instant accessTokenExpiresAt) {
}
