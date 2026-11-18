package dev.ioza.kingdom.bastion.domain.ports.out;

import org.springframework.http.ResponseCookie;

public interface KingdomPassportPort {

    ResponseCookie create(String sessionId);

    ResponseCookie destroy();

}
