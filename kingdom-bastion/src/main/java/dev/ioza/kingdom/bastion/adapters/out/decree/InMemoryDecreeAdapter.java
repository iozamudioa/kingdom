package dev.ioza.kingdom.bastion.adapters.out.decree;

import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;
import dev.ioza.kingdom.bastion.domain.model.identity.Vassal;
import dev.ioza.kingdom.bastion.domain.model.royal.RoyalDecree;
import dev.ioza.kingdom.bastion.domain.ports.out.DecreePort;

@Component
public class InMemoryDecreeAdapter implements DecreePort {

    private static final Map<String, Set<RoyalDecree>> vassalDecrees =
            Map.of("client-api", Set.of(new RoyalDecree("FLIGHT_CREATE")));

    @Override
    public Set<RoyalDecree> findFor(Vassal vassal) {
        return vassalDecrees.getOrDefault(vassal.id(), Set.of());
    }


}
