package dev.ioza.kingdom.tavern.emissary.infrastructure.heartbeat;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.core.env.Environment;
import dev.ioza.kingdom.contracts.TavernManifest;
import dev.ioza.kingdom.tavern.emissary.application.EmissaryService;
import dev.ioza.kingdom.tavern.emissary.domain.TavernManifestFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

public class TavernHeartbeat {

    private static final Logger log = Logger.getLogger(TavernHeartbeat.class.getName());

    private final TavernManifestFactory manifestFactory;
    private final EmissaryService emissaryService;
    private final Environment environment;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private volatile boolean registered;

    public TavernHeartbeat(TavernManifestFactory manifestFactory, EmissaryService emissaryService,
            Environment environment) {

        this.manifestFactory = manifestFactory;
        this.emissaryService = emissaryService;
        this.environment = environment;
    }

    @PostConstruct
    public void start() {

        scheduler.scheduleWithFixedDelay(this::heartbeat, 5, 30, TimeUnit.SECONDS);

    }

    @PreDestroy
    public void stop() {

        scheduler.shutdownNow();

    }

    private void heartbeat() {

        try {

            int port = Integer.parseInt(environment.getProperty("local.server.port"));

            manifestFactory.initialize(port);

            TavernManifest manifest = manifestFactory.create();

            log.info("Registering ... " + manifest.toString());

            emissaryService.register(manifest);

            if (!registered) {
                log.info("Tavern registered successfully.");
            }

            registered = true;

        } catch (Exception ex) {

            if (registered) {
                log.warning("Connection with Bastion lost.");
            }

            registered = false;

            log.log(Level.FINE, "Unable to register tavern. Will retry automatically.", ex);

        }

    }

}
