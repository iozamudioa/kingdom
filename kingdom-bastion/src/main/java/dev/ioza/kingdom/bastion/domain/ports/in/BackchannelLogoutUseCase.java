package dev.ioza.kingdom.bastion.domain.ports.in;

import reactor.core.publisher.Mono;

public interface BackchannelLogoutUseCase {

    Mono<Void> logout(String logoutToken);

}
