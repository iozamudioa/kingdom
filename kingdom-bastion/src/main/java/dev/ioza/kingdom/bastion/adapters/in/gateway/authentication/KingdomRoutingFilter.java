package dev.ioza.kingdom.bastion.adapters.in.gateway.authentication;

import java.net.URI;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import dev.ioza.kingdom.bastion.adapters.out.registry.TavernResolution;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class KingdomRoutingFilter implements GlobalFilter, Ordered {

    public static final String KINGDOM_RESOLUTION_ATTRIBUTE = "KINGDOM_RESOLUTION";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        TavernResolution resolution = exchange.getAttribute(KINGDOM_RESOLUTION_ATTRIBUTE);

        if (resolution == null) {
            return chain.filter(exchange);
        }

        URI target = UriComponentsBuilder.fromUri(resolution.tavern().target())
                .replacePath(resolution.forwardedPath())
                .replaceQuery(exchange.getRequest().getURI().getRawQuery()).build(true).toUri();

        log.info("Routing [{} {}] -> {}", exchange.getRequest().getMethod(),
                exchange.getRequest().getURI(), target);

        exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, target);


        log.info("REQUEST_URL={}",
                exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR), "");

        log.info("ROUTE={}", exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR), "");

        return chain.filter(exchange).doFinally(signal -> {

            log.info("FINAL REQUEST_URL={}",
                    exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR), "");

            log.info("FINAL ROUTE={}",
                    exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR), "");
        });

    }

    @Override
    public int getOrder() {
        return 10001;
    }

}
