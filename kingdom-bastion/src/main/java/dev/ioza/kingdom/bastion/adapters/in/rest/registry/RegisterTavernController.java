package dev.ioza.kingdom.bastion.adapters.in.rest.registry;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import dev.ioza.kingdom.bastion.application.registry.TavernRegistrationConflictException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import dev.ioza.kingdom.bastion.application.registry.RegisterTavernService;
import dev.ioza.kingdom.contracts.TavernManifest;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/internal/kingdom/taverns")
@RequiredArgsConstructor
public class RegisterTavernController {
    private final RegisterTavernService service;

    @ExceptionHandler(TavernRegistrationConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Mono<String> conflict(TavernRegistrationConflictException ex) {
        return Mono.just(ex.getMessage());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> register(@RequestBody TavernManifest manifest) {
        return service.register(manifest);
    }
}
