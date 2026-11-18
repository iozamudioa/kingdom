package dev.ioza.kingdom.bastion.application.audience;

import org.springframework.stereotype.Service;
import dev.ioza.kingdom.bastion.domain.ports.in.AudienceUseCase;
import dev.ioza.kingdom.bastion.domain.ports.out.AudienceStatePort;
import dev.ioza.kingdom.bastion.domain.ports.out.RealmPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AudienceService implements AudienceUseCase {

    private final AudienceStatePort audienceStatePort;

    private final RealmPort realmPort;

    @Override
    public Mono<AudienceResult> audience(AudienceCommand command) {

        return audienceStatePort.create(command.returnTo()).flatMap(realmPort::beginAudience)
                .map(AudienceResult::new);

    }

}
