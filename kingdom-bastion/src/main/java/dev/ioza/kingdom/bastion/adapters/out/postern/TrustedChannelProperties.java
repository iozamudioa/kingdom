package dev.ioza.kingdom.bastion.adapters.out.postern;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kingdom.postern")
public record TrustedChannelProperties(
        List<String> trustedNetworks,
        List<String> trustedIps,
        List<String> trustedProxies) {

    public TrustedChannelProperties {
        trustedNetworks = trustedNetworks == null ? List.of() : List.copyOf(trustedNetworks);
        trustedIps = trustedIps == null ? List.of() : List.copyOf(trustedIps);
        trustedProxies = trustedProxies == null ? List.of() : List.copyOf(trustedProxies);
    }
}
