package dev.ioza.kingdom.bastion.domain.ports.in;

import dev.ioza.kingdom.bastion.application.exile.ExileCommand;
import dev.ioza.kingdom.bastion.application.exile.ExileResult;
import reactor.core.publisher.Mono;

public interface ExileUseCase {

    Mono<ExileResult> exile(ExileCommand command);

}
