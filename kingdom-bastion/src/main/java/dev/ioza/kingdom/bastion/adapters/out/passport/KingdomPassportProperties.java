package dev.ioza.kingdom.bastion.adapters.out.passport;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kingdom.passport")
public record KingdomPassportProperties(Cookie cookie) {

}
