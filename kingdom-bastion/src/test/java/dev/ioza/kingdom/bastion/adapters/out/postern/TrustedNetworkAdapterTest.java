package dev.ioza.kingdom.bastion.adapters.out.postern;

import static org.junit.jupiter.api.Assertions.*;
import java.net.InetSocketAddress;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

class TrustedNetworkAdapterTest {
    private TrustedNetworkAdapter adapter() {
        return new TrustedNetworkAdapter(new TrustedChannelProperties(
                List.of("192.168.68.0/24"), List.of("10.8.0.2"), List.of("172.20.0.0/16")));
    }

    private InetSocketAddress address(String ip) {
        return new InetSocketAddress(IpRange.parseAddress(ip), 1234);
    }

    @Test void exactIpAndCidr() {
        assertTrue(adapter().identify(new HttpHeaders(), address("10.8.0.2")).isPresent());
        assertTrue(adapter().identify(new HttpHeaders(), address("192.168.68.51")).isPresent());
        assertTrue(adapter().identify(new HttpHeaders(), address("192.168.69.51")).isEmpty());
    }

    @Test void ignoresSpoofedHeadersFromUntrustedPeer() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Forwarded-For", "192.168.68.51");
        assertTrue(adapter().identify(headers, address("203.0.113.4")).isEmpty());
    }

    @Test void trustsNearestNonProxyHopOnly() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Forwarded-For", "192.168.68.51, 203.0.113.4");
        assertTrue(adapter().identify(headers, address("172.20.0.3")).isEmpty());
        headers.set("X-Forwarded-For", "192.168.68.51");
        assertTrue(adapter().identify(headers, address("172.20.0.3")).isPresent());
    }

    @Test void proxyWithoutForwardedClientIsNotTrusted() {
        assertTrue(adapter().identify(new HttpHeaders(), address("172.20.0.3")).isEmpty());
    }

    @Test void malformedHeaderFailsClosed() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Forwarded-For", "not-an-ip");
        assertTrue(adapter().identify(headers, address("172.20.0.3")).isEmpty());
    }

    @Test void cidrMatchesNetworkBoundary() {
        assertTrue(IpRange.parse("10.8.0.0/24").contains(IpRange.parseAddress("10.8.0.255")));
        assertFalse(IpRange.parse("10.8.0.0/24").contains(IpRange.parseAddress("10.8.1.1")));
    }
}
