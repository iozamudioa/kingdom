package dev.ioza.kingdom.bastion.domain.ports.out;

import java.net.URI;
import dev.ioza.kingdom.bastion.domain.model.identity.Vassal;
import dev.ioza.kingdom.bastion.domain.model.realm.RealmCredentials;
import dev.ioza.kingdom.bastion.domain.model.realm.RealmKnightAuthentication;
import reactor.core.publisher.Mono;

public interface RealmPort {

    Mono<Vassal> authenticateVassal(RealmCredentials credentials);

    default Mono<dev.ioza.kingdom.bastion.domain.model.identity.Knight> authenticateKnight(
            String username, String password) {
        return Mono.error(new UnsupportedOperationException("Knight credential authentication unavailable"));
    }

    Mono<URI> beginAudience(String state);

    Mono<RealmKnightAuthentication> authenticateKnight(String authorizationCode);

    Mono<RealmKnightAuthentication> refreshKnight(String refreshToken);

    Mono<Void> logout(String refreshToken);


}
