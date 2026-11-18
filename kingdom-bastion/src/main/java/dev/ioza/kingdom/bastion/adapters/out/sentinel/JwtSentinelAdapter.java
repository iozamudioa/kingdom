package dev.ioza.kingdom.bastion.adapters.out.sentinel;

import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import dev.ioza.kingdom.bastion.domain.model.parchment.Parchment;
import dev.ioza.kingdom.bastion.domain.model.parchment.ParchmentClaims;
import dev.ioza.kingdom.bastion.domain.ports.out.SentinelPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtSentinelAdapter implements SentinelPort {

    private final JwtEncoder jwtEncoder;
    private final JwtSentinelProperties properties;


    @Override
    public Parchment issue(ParchmentClaims parchmentClaims) {

        JwsHeader headers = JwsHeader.with(SignatureAlgorithm.RS256).type("JWT")
                .keyId(properties.keyId()).build();

        JwtClaimsSet claims = JwtClaimsSet.builder().issuer(properties.issuer())
                .subject(parchmentClaims.subject()).issuedAt(parchmentClaims.issuedAt())
                .expiresAt(parchmentClaims.expiresAt())
                .claim("identity_type", parchmentClaims.identityType())
                .claim("roles", parchmentClaims.roles()).claim("decrees", parchmentClaims.decrees())
                .build();

        Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(headers, claims));

        return new Parchment(jwt.getTokenValue(), jwt.getExpiresAt());
    }

}
