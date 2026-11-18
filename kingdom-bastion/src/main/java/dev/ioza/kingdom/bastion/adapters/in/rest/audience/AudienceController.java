package dev.ioza.kingdom.bastion.adapters.in.rest.audience;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ServerWebExchange;
import dev.ioza.kingdom.bastion.adapters.in.rest.common.Responses;
import dev.ioza.kingdom.bastion.application.audience.AudienceCommand;
import dev.ioza.kingdom.bastion.application.audience.callback.AudienceCallbackCommand;
import dev.ioza.kingdom.bastion.domain.ports.in.AudienceCallbackUseCase;
import dev.ioza.kingdom.bastion.domain.ports.in.AudienceUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/audience")
@RequiredArgsConstructor
@Slf4j
public class AudienceController {

    private final AudienceUseCase audienceUseCase;
    private final AudienceCallbackUseCase audienceCallbackUseCase;

    @GetMapping
    public Mono<Void> audience(@RequestParam String returnTo, ServerWebExchange exchange) {

        return audienceUseCase.audience(new AudienceCommand(returnTo))
                .flatMap(result -> Responses.redirect(exchange, result.location()));

    }

    @GetMapping("/callback")
    public Mono<Void> callback(@RequestParam String code, @RequestParam String state,
            ServerWebExchange exchange) {
        return audienceCallbackUseCase.callback(new AudienceCallbackCommand(code, state)).flatMap(
                result -> Responses.redirect(exchange, result.returnTo(), result.cookie()));

    }

}
