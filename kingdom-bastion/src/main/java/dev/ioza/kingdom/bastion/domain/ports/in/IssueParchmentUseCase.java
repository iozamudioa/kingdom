package dev.ioza.kingdom.bastion.domain.ports.in;

import dev.ioza.kingdom.bastion.domain.model.identity.Knight;
import dev.ioza.kingdom.bastion.domain.model.parchment.Parchment;

public interface IssueParchmentUseCase {

    Parchment issue(Knight knight);

}
