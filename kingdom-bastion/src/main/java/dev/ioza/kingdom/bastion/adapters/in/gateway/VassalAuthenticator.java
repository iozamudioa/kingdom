package dev.ioza.kingdom.bastion.adapters.in.gateway;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import dev.ioza.kingdom.contracts.TavernAccess;
import dev.ioza.kingdom.contracts.TavernManifest;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Order(2)
@Slf4j
public class VassalAuthenticator implements TavernAuthenticator {

    @Override
    public Mono<AuthenticationResult> authenticate(ServerWebExchange exchange,
            TavernManifest manifest) {

        String bearer = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (bearer == null) {
            return Mono.just(
                    new AuthenticationResult(AuthenticationStatus.NOT_AUTHENTICATED, exchange));
        }



        return Mono.just(new AuthenticationResult(AuthenticationStatus.AUTHENTICATED, exchange));
    }

    @Override
    public TavernAccess supports() {
        return TavernAccess.VASSAL;
    }


}
