package dev.ioza.kingdom.bastion.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import dev.ioza.kingdom.bastion.adapters.out.passport.KingdomPassportProperties;

@EnableConfigurationProperties(KingdomPassportProperties.class)
@Configuration
public class PassportConfiguration {
}
