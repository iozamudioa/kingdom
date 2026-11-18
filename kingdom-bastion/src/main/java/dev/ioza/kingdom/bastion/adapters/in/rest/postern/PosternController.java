package dev.ioza.kingdom.bastion.adapters.in.rest.postern;

import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ServerWebExchange;
import dev.ioza.kingdom.bastion.adapters.in.rest.common.Responses;
import dev.ioza.kingdom.bastion.application.postern.PosternCommand;
import dev.ioza.kingdom.bastion.domain.ports.in.PosternUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/postern")
@RequiredArgsConstructor
@Slf4j
public class PosternController {

        private final PosternUseCase posternUseCase;

        @GetMapping("/{tavern}")
        public Mono<Void> postern(@RequestParam Map<String, String> allParams,
                        @PathVariable String tavern, ServerWebExchange exchange) {
                return posternUseCase
                                .postern(new PosternCommand(tavern, allParams,
                                                exchange.getRequest().getHeaders(),
                                                exchange.getRequest().getRemoteAddress()))
                                .flatMap(result -> Responses.redirect(exchange, result.returnTo(),
                                                result.cookie()));

        }

}
