package dev.ioza.kingdom.tavern.lock.infrastructure.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import dev.ioza.kingdom.tavern.lock.infrastructure.properties.TavernLockProperties;
import dev.ioza.kingdom.tavern.lock.infrastructure.security.ParchmentAuthenticationConverter;

@AutoConfiguration
@EnableConfigurationProperties(TavernLockProperties.class)
@ConditionalOnProperty(prefix = "kingdom.tavern.lock", name = "enabled", havingValue = "true",
                matchIfMissing = true)

public class TavernLockAutoConfiguration {

        private static final Logger log =
                        LoggerFactory.getLogger(TavernLockAutoConfiguration.class);

        @Bean
        @ConditionalOnMissingBean
        JwtDecoder parchmentJwtDecoder(TavernLockProperties properties) {

                NimbusJwtDecoder decoder = NimbusJwtDecoder
                                .withJwkSetUri(properties.royalArchivesUri().toString()).build();

                decoder.setJwtValidator(JwtValidators
                                .createDefaultWithIssuer(properties.issuer().toString()));

                return decoder;
        }

        @Bean
        @ConditionalOnMissingBean
        ParchmentAuthenticationConverter parchmentAuthenticationConverter() {

                return new ParchmentAuthenticationConverter();
        }

        @Bean
        @ConditionalOnMissingBean(SecurityFilterChain.class)
        SecurityFilterChain tavernLockSecurityFilterChain(HttpSecurity http,
                        ParchmentAuthenticationConverter converter, TavernLockProperties properties)
                        throws Exception {

                log.info("Tavern Lock enabled Issuer.......... {} Royal Archives.. {}",
                                properties.issuer(), properties.royalArchivesUri());

                return http.csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(
                                                authorize -> authorize.anyRequest().authenticated())
                                .oauth2ResourceServer(resourceServer -> resourceServer.jwt(
                                                jwt -> jwt.jwtAuthenticationConverter(converter)))
                                .build();
        }
}
