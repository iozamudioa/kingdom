package dev.ioza.kingdom.tavern.lock.infrastructure.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@AutoConfiguration
@ConditionalOnProperty(prefix = "kingdom.tavern.lock", name = "enabled", havingValue = "false")
public class TavernLockDisabledAutoConfiguration {

    private static final Logger log =
            LoggerFactory.getLogger(TavernLockDisabledAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    SecurityFilterChain tavernLockDisabledSecurityFilterChain(HttpSecurity http) throws Exception {

        return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll()).build();
    }

    @Bean
    ApplicationRunner tavernLockDisabledLog() {
        return args -> log.warn("🏰 Tavern Lock disabled - all endpoints are permitted");
    }
}
