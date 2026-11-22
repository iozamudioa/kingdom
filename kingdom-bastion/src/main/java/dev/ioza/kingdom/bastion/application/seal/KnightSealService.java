package dev.ioza.kingdom.bastion.application.seal;
import java.time.Duration;
import java.time.Instant;
import org.springframework.stereotype.Service;
import dev.ioza.kingdom.bastion.domain.ports.out.RealmPort;
import dev.ioza.kingdom.bastion.domain.ports.in.IssueParchmentUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class KnightSealService {
    private final RealmPort realmPort;
    private final IssueParchmentUseCase issueParchment;
    public Mono<SealResult> seal(String username, String password) {
        return realmPort.authenticateKnight(username, password)
                .map(issueParchment::issue)
                .map(p -> SealResult.bearer(p.value(),
                        Duration.between(Instant.now(), p.expiresAt()).toSeconds()));
    }
}
