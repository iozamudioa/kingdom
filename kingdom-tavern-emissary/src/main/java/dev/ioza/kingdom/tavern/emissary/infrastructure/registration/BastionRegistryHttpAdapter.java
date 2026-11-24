package dev.ioza.kingdom.tavern.emissary.infrastructure.registration;

import org.springframework.web.client.RestClient;
import dev.ioza.kingdom.contracts.TavernManifest;
import java.util.Map;
import dev.ioza.kingdom.tavern.emissary.application.port.out.BastionRegistryPort;


public class BastionRegistryHttpAdapter implements BastionRegistryPort {

    private static final String REGISTRATION_PATH = "/internal/kingdom/taverns";

    private final RestClient restClient;

    public BastionRegistryHttpAdapter(String bastionUrl, String username, String password) {

        this.restClient = RestClient.builder().baseUrl(bastionUrl)
                .defaultHeaders(headers -> headers.setBasicAuth(username, password)).build();
    }

    @Override
    public void register(TavernManifest manifest) {
        restClient.post().uri(REGISTRATION_PATH).body(Map.of(
                "tavernId", manifest.tavernId(),
                "instanceId", manifest.instanceId(),
                "target", manifest.target().toString(),
                "healthPath", manifest.healthPath(),
                "version", manifest.version(),
                "startedAt", manifest.startedAt().toString()))
                .retrieve().toBodilessEntity();
    }

    @Override
    public void unregister(String instanceId) {
        throw new UnsupportedOperationException("Unimplemented method 'unregister'");
    }
}
