package dev.ioza.kingdom.bastion.adapters.in.gateway;

import org.springframework.web.server.ServerWebExchange;
import dev.ioza.kingdom.contracts.TavernAccess;
import dev.ioza.kingdom.contracts.TavernManifest;
import reactor.core.publisher.Mono;

public interface TavernAuthenticator {

    TavernAccess supports();

    Mono<AuthenticationResult> authenticate(ServerWebExchange exchange, TavernManifest tavern);

}
