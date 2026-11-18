package dev.ioza.kingdom.bastion.application.parchment;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import org.springframework.stereotype.Service;
import dev.ioza.kingdom.bastion.domain.model.identity.Knight;
import dev.ioza.kingdom.bastion.domain.model.parchment.Parchment;
import dev.ioza.kingdom.bastion.domain.model.parchment.ParchmentClaims;
import dev.ioza.kingdom.bastion.domain.ports.in.IssueParchmentUseCase;
import dev.ioza.kingdom.bastion.domain.ports.out.SentinelPort;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IssueParchmentService implements IssueParchmentUseCase {

    private final SentinelPort sentinel;

    @Override
    public Parchment issue(Knight knight) {

        ParchmentClaims claims = new ParchmentClaims(knight.username(), "KNIGHT", knight.roles(),
                Set.of(), Instant.now(), Instant.now().plus(Duration.ofMinutes(15)));

        return sentinel.issue(claims);
    }



}
