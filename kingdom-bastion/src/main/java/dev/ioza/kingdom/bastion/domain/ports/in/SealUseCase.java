package dev.ioza.kingdom.bastion.domain.ports.in;

import dev.ioza.kingdom.bastion.application.seal.SealCommand;
import dev.ioza.kingdom.bastion.application.seal.SealResult;
import reactor.core.publisher.Mono;

public interface SealUseCase {

    Mono<SealResult> seal(SealCommand command);

}
