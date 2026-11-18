package dev.ioza.kingdom.bastion.configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfiguration {


    @Bean
    RouteLocator kingdomRoute(RouteLocatorBuilder builder) {

        return builder.routes().route("kingdom", route -> route.path("/**").uri("no://op")).build();
    }

}
