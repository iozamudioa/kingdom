package dev.ioza.kingdom.bastion.adapters.out.postern;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import dev.ioza.kingdom.bastion.domain.model.postern.TrustedChannel;
import dev.ioza.kingdom.bastion.domain.ports.out.TrustedChannelPort;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TrustedNetworkAdapter implements TrustedChannelPort {

    private final List<IpRange> networks;
    private final List<IpRange> ips;
    private final List<IpRange> proxies;

    public TrustedNetworkAdapter(TrustedChannelProperties properties) {
        networks = properties.trustedNetworks().stream().map(IpRange::parse).toList();
        ips = properties.trustedIps().stream().map(IpRange::parse)
                .peek(range -> {
                    if (range.prefixLength() != range.network().length * 8) {
                        throw new IllegalArgumentException("trusted-ips must contain exact addresses");
                    }
                }).toList();
        proxies = properties.trustedProxies().stream().map(IpRange::parse).toList();
    }

    @Override
    public Optional<TrustedChannel> identify(HttpHeaders headers, InetSocketAddress remoteAddress) {
        if (remoteAddress == null || remoteAddress.getAddress() == null) {
            return Optional.empty();
        }

        InetAddress client;
        try {
            client = resolveClient(headers, remoteAddress.getAddress());
        } catch (IllegalArgumentException ex) {
            log.warn("Rejecting malformed forwarded client IP");
            return Optional.empty();
        }

        boolean trusted = client.isLoopbackAddress()
                || networks.stream().anyMatch(range -> range.contains(client))
                || ips.stream().anyMatch(range -> range.contains(client));
        return trusted ? Optional.of(new TrustedChannel(client.getHostAddress())) : Optional.empty();
    }

    private InetAddress resolveClient(HttpHeaders headers, InetAddress peer) {
        // Never trust a forwarding header supplied directly by an untrusted peer.
        if (proxies.stream().noneMatch(range -> range.contains(peer))) {
            return peer;
        }
        List<String> forwarded = headers.getOrEmpty("X-Forwarded-For");
        if (forwarded.isEmpty()) {
            // A proxy's own IP is NOT proof that the original client is trusted.
            throw new IllegalArgumentException("Missing X-Forwarded-For from trusted proxy");
        }

        List<InetAddress> chain = new ArrayList<>();
        for (String header : forwarded) {
            for (String part : header.split(",", -1)) {
                chain.add(IpRange.parseAddress(part.trim()));
            }
        }
        // Walk from the closest proxy toward the client. A client-supplied prefix
        // is never trusted because we stop at the first untrusted hop.
        for (int i = chain.size() - 1; i >= 0; i--) {
            InetAddress hop = chain.get(i);
            if (proxies.stream().noneMatch(range -> range.contains(hop))) {
                return hop;
            }
        }
        throw new IllegalArgumentException("No client hop in forwarded chain");
    }
}
