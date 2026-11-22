package dev.ioza.kingdom.tavern.emissary.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import org.springframework.core.env.Environment;
import dev.ioza.kingdom.contracts.TavernManifest;
import dev.ioza.kingdom.contracts.TavernRoutePredicate;
import dev.ioza.kingdom.tavern.emissary.infrastructure.properties.TavernEmissaryProperties;

public class TavernManifestFactory {

    private static final Logger log = Logger.getLogger(TavernManifestFactory.class.getName());

    private final TavernEmissaryProperties properties;
    private final Environment environment;

    private String instanceId;
    private Instant startedAt;
    private String application;
    private int actualPort;
    private String version;

    public TavernManifestFactory(TavernEmissaryProperties properties, Environment environment) {
        this.properties = properties;
        this.environment = environment;

        Package pkg = getClass().getPackage();
        this.version = Optional.ofNullable(pkg.getImplementationVersion()).orElse("dev");


    }

    public synchronized void initialize(int actualPort) {

        if (instanceId != null) {
            return;
        }

        this.actualPort = actualPort;
        this.startedAt = Instant.now();
        this.application =
                environment.getProperty("spring.application.name", properties.tavernName());

        this.instanceId = createInstanceId();
    }

    public TavernManifest create() {

        List<TavernRoutePredicate> predicates =
                List.of(TavernRoutePredicate.domain(properties.domain()),
                        TavernRoutePredicate.path(properties.path()));

        return new TavernManifest(application, instanceId, properties.target(),
                properties.healthPath(), version, startedAt, properties.capabilities(), predicates,
                properties.publicPaths());
    }

    private String createInstanceId() {
        return application + "-" + actualPort + "-" + startedAt.toEpochMilli();
    }
}
