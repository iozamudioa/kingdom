package dev.ioza.kingdom.bastion.adapters.in.rest.seal;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import dev.ioza.kingdom.bastion.application.seal.KnightSealService;
import dev.ioza.kingdom.bastion.domain.model.realm.InvalidRealmCredentialsException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class KnightSealController {
    private final KnightSealService service;
    @PostMapping(path="/seal/knight", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<SealResponse>> seal(@Valid @RequestBody KnightSealRequest request) {
        return service.seal(request.username(), request.password())
                .map(result -> ResponseEntity.ok().cacheControl(CacheControl.noStore())
                        .header(HttpHeaders.PRAGMA, "no-cache").body(SealResponse.from(result)))
                .onErrorResume(InvalidRealmCredentialsException.class,
                        ex -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }
}
