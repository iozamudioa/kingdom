package dev.ioza.kingdom.bastion.configuration;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import dev.ioza.kingdom.bastion.adapters.out.sentinel.JwtSentinelAdapter;
import dev.ioza.kingdom.bastion.adapters.out.sentinel.JwtSentinelProperties;
import dev.ioza.kingdom.bastion.domain.ports.out.SentinelPort;

@Configuration
@EnableConfigurationProperties(JwtSentinelProperties.class)
public class SentinelConfiguration {
    @Bean
    JwtEncoder jwtEncoder(JwtSentinelProperties properties) {
        RSAPublicKey publicKey = PemKeyReader.readPublicKey(properties.publicKey());

        RSAPrivateKey privateKey = PemKeyReader.readPrivateKey(properties.privateKey());

        RSAKey rsaKey =
                new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(properties.keyId())
                        .keyUse(KeyUse.SIGNATURE).algorithm(JWSAlgorithm.RS256).build();

        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(rsaKey));

        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    SentinelPort sentinelPort(JwtEncoder jwtEncoder, JwtSentinelProperties properties) {
        return new JwtSentinelAdapter(jwtEncoder, properties);
    }

    @Bean
    RSAKey bastionRsaKey(JwtSentinelProperties properties) {

        RSAPublicKey publicKey = PemKeyReader.readPublicKey(properties.publicKey());

        RSAPrivateKey privateKey = PemKeyReader.readPrivateKey(properties.privateKey());

        return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(properties.keyId())
                .keyUse(KeyUse.SIGNATURE).algorithm(JWSAlgorithm.RS256).build();
    }
}
