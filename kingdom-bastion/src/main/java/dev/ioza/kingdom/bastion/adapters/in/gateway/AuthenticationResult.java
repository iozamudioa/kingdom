package dev.ioza.kingdom.bastion.adapters.in.gateway;

import org.springframework.web.server.ServerWebExchange;

public record AuthenticationResult(

        AuthenticationStatus status,

        ServerWebExchange exchange

) {
}
