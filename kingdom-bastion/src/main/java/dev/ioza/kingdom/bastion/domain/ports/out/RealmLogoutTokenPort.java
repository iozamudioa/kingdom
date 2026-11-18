package dev.ioza.kingdom.bastion.domain.ports.out;

import dev.ioza.kingdom.bastion.domain.model.realm.RealmLogout;
import reactor.core.publisher.Mono;

public interface RealmLogoutTokenPort {

    Mono<RealmLogout> validate(String logoutToken);

}
