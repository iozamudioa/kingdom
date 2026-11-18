package dev.ioza.kingdom.tavern.lock.infrastructure.security;

import java.util.Collection;
import java.util.List;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class RoyalDecreeAuthoritiesConverter
        implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String DECREES_CLAIM = "decrees";

    @Override
    public Collection<GrantedAuthority> convert(Jwt parchment) {

        List<String> decrees = parchment.getClaimAsStringList(DECREES_CLAIM);

        if (decrees == null || decrees.isEmpty()) {
            return List.of();
        }

        return decrees.stream().filter(decree -> decree != null && !decree.isBlank()).distinct()
                .map(SimpleGrantedAuthority::new).map(GrantedAuthority.class::cast).toList();
    }
}
