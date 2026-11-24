package dev.ioza.kingdom.bastion.adapters.in.rest.registry;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import dev.ioza.kingdom.bastion.application.registry.TavernDefinitionService;
import dev.ioza.kingdom.bastion.application.registry.TavernRegistrationConflictException;
import dev.ioza.kingdom.bastion.domain.model.registry.TavernDefinition;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/internal/kingdom/tavern-definitions")
@RequiredArgsConstructor
public class TavernDefinitionController {
    private final TavernDefinitionService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TavernDefinition> create(@RequestBody TavernDefinition definition) {
        return service.create(definition);
    }

    @ExceptionHandler(TavernRegistrationConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Mono<String> conflict(TavernRegistrationConflictException ex) {
        return Mono.just(ex.getMessage());
    }
}
