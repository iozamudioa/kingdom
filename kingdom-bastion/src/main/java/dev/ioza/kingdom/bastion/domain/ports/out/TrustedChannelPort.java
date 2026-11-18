package dev.ioza.kingdom.bastion.domain.ports.out;

import java.net.InetSocketAddress;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import dev.ioza.kingdom.bastion.domain.model.postern.TrustedChannel;

public interface TrustedChannelPort {

    Optional<TrustedChannel> identify(HttpHeaders headers, InetSocketAddress remoteAddress);

}
