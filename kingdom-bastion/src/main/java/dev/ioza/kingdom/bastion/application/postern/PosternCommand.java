package dev.ioza.kingdom.bastion.application.postern;

import java.net.InetSocketAddress;
import java.util.Map;
import org.springframework.http.HttpHeaders;

public record PosternCommand(String tavern, Map<String, String> parameters, HttpHeaders headers,
        InetSocketAddress remoteAddress) {

}
