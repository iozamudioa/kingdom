package dev.ioza.kingdom.bastion.adapters.in.rest.common;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public final class Responses {

    private Responses() {}

    public static Mono<Void> redirect(ServerWebExchange exchange, URI location,
            ResponseCookie... cookies) {

        for (ResponseCookie cookie : cookies) {
            if (cookie != null) {
                exchange.getResponse().addCookie(cookie);
            }
        }

        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
        exchange.getResponse().getHeaders().setLocation(location);
        return exchange.getResponse().setComplete();

    }

}
