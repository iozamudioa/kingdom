package dev.ioza.kingdom.bastion.application.registry;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import dev.ioza.kingdom.bastion.domain.ports.out.GatewayRoutesPort;
import dev.ioza.kingdom.bastion.domain.ports.out.TavernPersistencePort;
import dev.ioza.kingdom.bastion.domain.ports.out.TavernRegistryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoadTavernsService {
    private final TavernPersistencePort persistence;
    private final TavernRegistryPort registry;
    private final GatewayRoutesPort routes;

    @EventListener(ApplicationReadyEvent.class)
    public void restore() {
        AtomicInteger count = new AtomicInteger();
        persistence.findAll()
                .doOnNext(manifest -> {
                    registry.save(manifest);
                    count.incrementAndGet();
                })
                .doOnComplete(() -> {
                    routes.refresh();
                    log.info("Restored {} taverns from MongoDB", count.get());
                })
                .doOnError(ex -> log.error("Unable to restore taverns from MongoDB", ex))
                .subscribe();
    }
}
