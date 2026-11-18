package dev.ioza.kingdom.bastion.adapters.out.postern;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kingdom.postern")
public record TrustedChannelProperties(List<String> trustedNetworks) {

}
