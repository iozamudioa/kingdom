package dev.ioza.kingdom.bastion.domain.ports.out;

import dev.ioza.kingdom.bastion.domain.model.parchment.Parchment;
import dev.ioza.kingdom.bastion.domain.model.parchment.ParchmentClaims;

public interface SentinelPort {

    Parchment issue(ParchmentClaims claims);

}
