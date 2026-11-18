package dev.ioza.kingdom.bastion.adapters.in.rest.seal;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dev.ioza.kingdom.bastion.application.seal.SealCommand;
import dev.ioza.kingdom.bastion.domain.ports.in.SealUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/seal")
@RequiredArgsConstructor
public class SealController {

    private final SealUseCase sealUseCase;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<SealResponse>> seal(@Valid @RequestBody SealRequest request) {

        SealCommand command = new SealCommand(request.clientId(), request.clientSecret());

        return sealUseCase.seal(command)
                .map(result -> ResponseEntity.ok(SealResponse.from(result)));
    }

}
