package dev.ioza.kingdom.tavern.lock.infrastructure.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

public class ParchmentAuthenticationConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtAuthenticationConverter delegate;

    public ParchmentAuthenticationConverter() {

        RoyalDecreeAuthoritiesConverter decreesConverter = new RoyalDecreeAuthoritiesConverter();

        this.delegate = new JwtAuthenticationConverter();
        this.delegate.setJwtGrantedAuthoritiesConverter(decreesConverter);
        this.delegate.setPrincipalClaimName("sub");
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt parchment) {
        return delegate.convert(parchment);
    }
}
