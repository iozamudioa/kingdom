package dev.ioza.kingdom.bastion.application.postern;

import java.net.URI;
import java.util.Optional;
import org.springframework.stereotype.Component;
import dev.ioza.kingdom.bastion.domain.model.identity.Knight;
import dev.ioza.kingdom.bastion.domain.model.identity.KnightSession;
import dev.ioza.kingdom.bastion.domain.model.postern.InvalidCarriageException;
import dev.ioza.kingdom.bastion.domain.model.postern.TrustedChannel;
import dev.ioza.kingdom.bastion.domain.ports.in.PosternUseCase;
import dev.ioza.kingdom.bastion.domain.ports.out.KingdomPassportPort;
import dev.ioza.kingdom.bastion.domain.ports.out.KnightSessionPort;
import dev.ioza.kingdom.bastion.domain.ports.out.TrustedChannelPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class PosternService implements PosternUseCase {

    private final TrustedChannelPort trustedChannelPort;
    private final KnightSessionPort knightSessionPort;
    private final KingdomPassportPort kingdomPassportPort;

    @Override
    public Mono<PosternResult> postern(PosternCommand command) {

        Optional<TrustedChannel> trustedChannel =
                trustedChannelPort.identify(command.headers(), command.remoteAddress());

        if (trustedChannel.isEmpty()) {
            return Mono.error(new InvalidCarriageException());
        }

        Knight knight = Knight.trusted(trustedChannel.get().name());

        String parameters = command.parameters().entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue()).reduce((a, b) -> a + "&" + b)
                .orElse("");

        String path = "/" + command.tavern() + "/";

        String url = parameters.isEmpty() ? path : path + "?" + parameters;

        log.info("Knight {} is being sent to url {}", knight.username(), url);

        URI returnTo = URI.create(url);

        KnightSession knightSession = KnightSession.trusted(knight);

        return knightSessionPort.save(knightSession).map(session -> new PosternResult(returnTo,
                kingdomPassportPort.create(session.passport())));

    }

}
