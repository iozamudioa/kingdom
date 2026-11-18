package dev.ioza.kingdom.bastion.application.exile;

import java.net.URI;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import dev.ioza.kingdom.bastion.domain.ports.in.ExileUseCase;
import dev.ioza.kingdom.bastion.domain.ports.out.KingdomPassportPort;
import dev.ioza.kingdom.bastion.domain.ports.out.KnightSessionPort;
import dev.ioza.kingdom.bastion.domain.ports.out.RealmPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExileService implements ExileUseCase {

    private final KnightSessionPort knightSessionPort;
    private final KingdomPassportPort kingdomPassportPort;
    private final RealmPort realmPort;

    @Override
    public Mono<ExileResult> exile(ExileCommand command) {

        if (!StringUtils.hasText(command.sessionId())) {
            return returnToTavernHome(command.tavern());
        }

        return knightSessionPort.find(command.sessionId())

                .flatMap(session -> realmPort.logout(session.refreshToken())

                        .doOnSuccess(ignored -> log.info("Realm session closed for passport {}",
                                session.passport()))

                        .onErrorResume(ex -> {
                            log.warn("Could not close Realm session for passport {}",
                                    session.passport(), ex);
                            return Mono.empty();
                        })

                        .then(knightSessionPort.destroy(session.passport())))

                .switchIfEmpty(knightSessionPort.destroy(command.sessionId()))

                .then(returnToTavernHome(command.tavern()));
    }

    private Mono<ExileResult> returnToTavernHome(String tavern) {

        return Mono.just(
                new ExileResult(URI.create("/" + tavern + "/"), kingdomPassportPort.destroy()));
    }

}
