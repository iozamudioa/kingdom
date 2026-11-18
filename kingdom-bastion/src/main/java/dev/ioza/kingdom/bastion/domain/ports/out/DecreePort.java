package dev.ioza.kingdom.bastion.domain.ports.out;

import java.util.Set;
import dev.ioza.kingdom.bastion.domain.model.identity.Vassal;
import dev.ioza.kingdom.bastion.domain.model.royal.RoyalDecree;

public interface DecreePort {

    Set<RoyalDecree> findFor(Vassal vassal);

}
