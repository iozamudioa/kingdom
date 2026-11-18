package dev.ioza.kingdom.bastion.adapters.in.rest.exile;

import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ServerWebExchange;
import dev.ioza.kingdom.bastion.adapters.in.rest.common.Responses;
import dev.ioza.kingdom.bastion.application.exile.ExileCommand;
import dev.ioza.kingdom.bastion.domain.ports.in.ExileUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ExileController {

    private static final String PASSPORT_COOKIE = "KINGDOM_PASSPORT";

    private final ExileUseCase exileUseCase;

    @GetMapping("/{tavern}/exile")
    public Mono<Void> exile(@PathVariable String tavern, ServerWebExchange exchange) {

        HttpCookie passport = exchange.getRequest().getCookies().getFirst(PASSPORT_COOKIE);

        String sessionId = passport != null ? passport.getValue() : null;

        return exileUseCase.exile(new ExileCommand(tavern, sessionId)).flatMap(
                result -> Responses.redirect(exchange, result.returnTo(), result.cookie()));

    }

}
