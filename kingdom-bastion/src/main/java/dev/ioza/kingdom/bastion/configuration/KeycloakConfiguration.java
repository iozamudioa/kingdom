package dev.ioza.kingdom.bastion.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.web.reactive.function.client.WebClient;
import dev.ioza.kingdom.bastion.adapters.out.realm.KeycloakRealmAdapter;
import dev.ioza.kingdom.bastion.adapters.out.realm.KeycloakRealmProperties;
import dev.ioza.kingdom.bastion.domain.ports.out.RealmPort;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableConfigurationProperties(KeycloakRealmProperties.class)
@Slf4j
public class KeycloakConfiguration {

    @Bean
    WebClient realmWebClient(KeycloakRealmProperties properties) {
        return WebClient.builder().baseUrl(properties.baseUrl()).build();
    }

    @Bean
    RealmPort realmPort(WebClient keycloakWebClient, KeycloakRealmProperties properties,
            @Qualifier("realmJwtDecoder") ReactiveJwtDecoder realmJwtDecoder) {
        return new KeycloakRealmAdapter(keycloakWebClient, properties, realmJwtDecoder);
    }

    @Bean("realmJwtDecoder")
    ReactiveJwtDecoder realmJwtDecoder(KeycloakRealmProperties properties) {

        NimbusReactiveJwtDecoder decoder =
                NimbusReactiveJwtDecoder.withJwkSetUri(properties.jwkSetUri()).build();

        OAuth2TokenValidator<Jwt> issuerValidator =
                JwtValidators.createDefaultWithIssuer(properties.issuerUri());

        decoder.setJwtValidator(issuerValidator);

        return decoder;
    }


    @Bean("realmLogoutJwtDecoder")
    ReactiveJwtDecoder realmLogoutJwtDecoder(KeycloakRealmProperties properties) {

        NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder
                .withJwkSetUri(properties.jwkSetUri()).validateType(false).build();

        /*
         * IMPORTANTE: Esto reemplaza el validador predeterminado que exige typ=JWT.
         */
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(new JwtTimestampValidator(),
                new JwtIssuerValidator(properties.issuerUri())));

        return decoder;
    }
}
