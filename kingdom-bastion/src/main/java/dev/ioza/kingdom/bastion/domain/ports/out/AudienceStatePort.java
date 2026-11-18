package dev.ioza.kingdom.bastion.domain.ports.out;

import reactor.core.publisher.Mono;

public interface AudienceStatePort {

    Mono<String> create(String returnTo);

    Mono<String> consume(String state);

}
