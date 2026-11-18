package dev.ioza.kingdom.tavern.host.configuration;

import java.net.URI;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@ConditionalOnProperty(prefix = "kingdom.tavern.host", name = "mode", havingValue = "PROXY")
public class ProxyConfiguration {

    @Bean
    RestClient proxyRestClient(TavernHostProperties properties) {

        return RestClient.builder().baseUrl(URI.create(properties.source()).toString()).build();

    }

}
