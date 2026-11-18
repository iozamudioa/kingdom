package dev.ioza.kingdom.bastion.domain.ports.in;

import dev.ioza.kingdom.bastion.application.audience.callback.AudienceCallbackCommand;
import dev.ioza.kingdom.bastion.application.audience.callback.AudienceCallbackResult;
import reactor.core.publisher.Mono;

public interface AudienceCallbackUseCase {

    Mono<AudienceCallbackResult> callback(AudienceCallbackCommand command);

}
