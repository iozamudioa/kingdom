package dev.ioza.kingdom.bastion.adapters.out.postern;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import dev.ioza.kingdom.bastion.domain.model.postern.TrustedChannel;
import dev.ioza.kingdom.bastion.domain.ports.out.TrustedChannelPort;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TrustedNetworkAdapter implements TrustedChannelPort {

    private static final String LOCALHOST = "localhost";

    private final TrustedChannelProperties properties;

    public TrustedNetworkAdapter(TrustedChannelProperties properties) {
        this.properties = properties;
        log.info("properties list {}", properties.trustedNetworks());
    }

    @Override
    public Optional<TrustedChannel> identify(HttpHeaders headers, InetSocketAddress remoteAddress) {

        InetAddress address = resolve(headers, remoteAddress);

        log.info("Client IP: {}", address.getHostAddress());

        if (address.isLoopbackAddress()) {
            return Optional.of(new TrustedChannel(LOCALHOST));
        }

        String ip = address.getHostAddress();

        log.info("properties: {}", Arrays.asList(properties.trustedNetworks().toArray()));

        return properties.trustedNetworks().stream().anyMatch(ip::startsWith)
                ? Optional.of(new TrustedChannel(ip))
                : Optional.empty();

    }

    private InetAddress resolve(HttpHeaders headers, InetSocketAddress remoteAddress) {

        String forwardedFor = headers.getFirst("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {

            try {
                return InetAddress.getByName(forwardedFor.split(",")[0].trim());

            } catch (Exception ignored) {
                log.warn("Invalid X-Forwarded-For '{}'", forwardedFor);
            }

        }

        InetSocketAddress remote = remoteAddress;

        if (remote != null && remote.getAddress() != null) {
            return remote.getAddress();
        }

        throw new IllegalStateException("Unable to resolve client address.");

    }

}
