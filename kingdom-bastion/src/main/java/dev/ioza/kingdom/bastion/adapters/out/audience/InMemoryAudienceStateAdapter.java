package dev.ioza.kingdom.bastion.adapters.out.audience;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import dev.ioza.kingdom.bastion.domain.ports.out.AudienceStatePort;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class InMemoryAudienceStateAdapter implements AudienceStatePort {
    private final Map<String, String> states = new ConcurrentHashMap<>();

    @Override
    public Mono<String> create(String returnTo) {

        String state = UUID.randomUUID().toString();

        states.put(state, returnTo);

        log.debug("Created state: {}", states);

        return Mono.just(state);

    }

    @Override
    public Mono<String> consume(String state) {

        log.debug("Consumed state: {}", states);

        return Mono.justOrEmpty(states.remove(state));

    }

}
