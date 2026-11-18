package dev.ioza.kingdom.bastion.configuration;

import java.security.interfaces.RSAPublicKey;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import dev.ioza.kingdom.bastion.adapters.out.postern.TrustedChannelProperties;
import dev.ioza.kingdom.bastion.adapters.out.sentinel.JwtSentinelProperties;

@Configuration
@EnableConfigurationProperties(TrustedChannelProperties.class)
public class SecurityConfiguration {

        @Bean
        CommandLineRunner test(TrustedChannelProperties p) {
                return args -> System.out.println(">>>> " + p.trustedNetworks());
        }

        @Bean
        SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http,
                        @Qualifier("bastionJwtDecoder") ReactiveJwtDecoder bastionJwtDecoder)
                        throws Exception {

                return http.csrf(csrf -> csrf.disable()).authorizeExchange(authorize -> authorize
                                .pathMatchers("/seal", "/royal-archives", "/audience",
                                                "/audience/callback", "/test", "/", "/**",
                                                "/audience/backchannel-logout")
                                .permitAll()
                                .pathMatchers("/internal/kingdom/taverns",
                                                "/internal/kingdom/health")
                                .hasRole("TAVERN").anyExchange().authenticated())
                                .httpBasic(Customizer.withDefaults())
                                .oauth2ResourceServer(resource -> resource
                                                .jwt(jwt -> jwt.jwtDecoder(bastionJwtDecoder)))
                                .build();
        }


        @Bean("bastionJwtDecoder")
        ReactiveJwtDecoder jwtDecoder(RSAKey bastionRsaKey, JwtSentinelProperties properties)
                        throws JOSEException {

                RSAPublicKey publicKey = bastionRsaKey.toRSAPublicKey();

                NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder.withPublicKey(publicKey)
                                .signatureAlgorithm(SignatureAlgorithm.RS256).build();

                decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(properties.issuer()));

                return decoder;
        }

        @Bean
        MapReactiveUserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {

                var tavernUser = User.builder().username("tavern")
                                .password(passwordEncoder.encode("kingdom")).roles("TAVERN")
                                .build();

                return new MapReactiveUserDetailsService(tavernUser);
        }

        @Bean
        PasswordEncoder passwordEncoder() {
                return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        }


}
