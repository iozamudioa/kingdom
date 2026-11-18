package dev.ioza.kingdom.bastion.application.audience.callback;

import java.net.URI;
import org.springframework.stereotype.Service;
import dev.ioza.kingdom.bastion.domain.model.identity.KnightSession;
import dev.ioza.kingdom.bastion.domain.model.realm.InvalidAudienceStateException;
import dev.ioza.kingdom.bastion.domain.ports.in.AudienceCallbackUseCase;
import dev.ioza.kingdom.bastion.domain.ports.out.AudienceStatePort;
import dev.ioza.kingdom.bastion.domain.ports.out.KingdomPassportPort;
import dev.ioza.kingdom.bastion.domain.ports.out.KnightSessionPort;
import dev.ioza.kingdom.bastion.domain.ports.out.RealmPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AudienceCallbackService implements AudienceCallbackUseCase {

        private final AudienceStatePort audienceStatePort;

        private final RealmPort realmPort;
        private final KnightSessionPort knightSessionPort;
        private final KingdomPassportPort kingdomPassportPort;

        @Override
        public Mono<AudienceCallbackResult> callback(AudienceCallbackCommand command) {

                return audienceStatePort.consume(command.state())
                                .switchIfEmpty(Mono.error(new InvalidAudienceStateException()))
                                .flatMap(returnTo -> realmPort.authenticateKnight(command.code())
                                                .flatMap(auth -> knightSessionPort
                                                                .save(KnightSession.realm(
                                                                                auth.knight(),
                                                                                auth.realmSessionId(),
                                                                                auth.accessToken(),
                                                                                auth.refreshToken(),
                                                                                auth.idToken(),
                                                                                auth.accessTokenExpiresAt()))
                                                                .map(session -> {
                                                                        return new AudienceCallbackResult(
                                                                                        URI.create(returnTo),
                                                                                        kingdomPassportPort
                                                                                                        .create(session.passport()));
                                                                })));

        }


}
