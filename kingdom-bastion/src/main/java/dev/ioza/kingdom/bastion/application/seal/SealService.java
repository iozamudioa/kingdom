package dev.ioza.kingdom.bastion.application.seal;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import dev.ioza.kingdom.bastion.domain.model.parchment.Parchment;
import dev.ioza.kingdom.bastion.domain.model.parchment.ParchmentClaims;
import dev.ioza.kingdom.bastion.domain.model.realm.RealmCredentials;
import dev.ioza.kingdom.bastion.domain.model.royal.RoyalDecree;
import dev.ioza.kingdom.bastion.domain.ports.in.SealUseCase;
import dev.ioza.kingdom.bastion.domain.ports.out.DecreePort;
import dev.ioza.kingdom.bastion.domain.ports.out.RealmPort;
import dev.ioza.kingdom.bastion.domain.ports.out.SentinelPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class SealService implements SealUseCase {

        private static final Duration PARCHMENT_TTL = Duration.ofMinutes(15);

        private final SentinelPort sentinelPort;
        private final DecreePort decreePort;
        private final RealmPort realmPort;
        private final Clock clock;

        @Override
        public Mono<SealResult> seal(SealCommand command) {
                RealmCredentials realmCredentials =
                                new RealmCredentials(command.clientId(), command.clientSecret());

                return realmPort.authenticateVassal(realmCredentials).map(vassal -> {
                        Set<RoyalDecree> royalDecrees = decreePort.findFor(vassal);

                        Instant issuedAt = clock.instant();
                        Instant expiresAt = issuedAt.plus(PARCHMENT_TTL);

                        Set<String> decrees = royalDecrees.stream().map(RoyalDecree::authority)
                                        .collect(Collectors.toUnmodifiableSet());

                        ParchmentClaims claims = new ParchmentClaims(vassal.id(), "VASSAL",
                                        vassal.roles(), decrees, issuedAt, expiresAt);

                        Parchment parchment = sentinelPort.issue(claims);

                        long expiresIn = Duration.between(issuedAt, parchment.expiresAt())
                                        .toSeconds();

                        return SealResult.bearer(parchment.value(), expiresIn);
                });
        }

}
