package dev.ioza.kingdom.bastion.adapters.out.sentinel;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties(prefix = "kingdom.sentinel.jwt")
public record JwtSentinelProperties(

        String issuer,

        String keyId,

        Resource publicKey,

        Resource privateKey

) {

    public JwtSentinelProperties {
        if (issuer == null || issuer.isBlank()) {
            throw new IllegalArgumentException("JWT issuer is required");
        }

        if (keyId == null || keyId.isBlank()) {
            throw new IllegalArgumentException("JWT keyId is required");
        }

        if (publicKey == null) {
            throw new IllegalArgumentException("JWT public key is required");
        }

        if (privateKey == null) {
            throw new IllegalArgumentException("JWT private key is required");
        }
    }
}
