package dev.ioza.kingdom.bastion.domain.ports.in;

import dev.ioza.kingdom.bastion.application.audience.AudienceCommand;
import dev.ioza.kingdom.bastion.application.audience.AudienceResult;
import reactor.core.publisher.Mono;

public interface AudienceUseCase {

    Mono<AudienceResult> audience(AudienceCommand command);
}
