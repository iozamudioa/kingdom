package dev.ioza.kingdom.bastion.adapters.in.rest.audience.logout;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import dev.ioza.kingdom.bastion.domain.ports.in.BackchannelLogoutUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BackchannelLogoutController {

    private final BackchannelLogoutUseCase backchannelLogoutUseCase;

    @PostMapping(value = "/audience/backchannel-logout",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Mono<ResponseEntity<Void>> logout(ServerWebExchange exchange) {

        return exchange.getFormData().map(form -> form.getFirst("logout_token"))
                .flatMap(backchannelLogoutUseCase::logout).thenReturn(ResponseEntity.ok().build());

    }
}
