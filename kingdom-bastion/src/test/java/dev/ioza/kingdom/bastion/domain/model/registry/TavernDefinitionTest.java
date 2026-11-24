package dev.ioza.kingdom.bastion.domain.model.registry;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import dev.ioza.kingdom.contracts.TavernAccess;

class TavernDefinitionTest {
    @Test
    void missingAllowedAccessDefaultsToKnight() {
        TavernDefinition definition = new TavernDefinition("bank", "bank.ioza.dev",
                "/**", "/bank", null, List.of("ui"), List.of("/assets/**"), true);
        assertEquals(List.of(TavernAccess.KNIGHT), definition.allowedAccess());
    }

    @Test
    void emptyAllowedAccessDefaultsToKnight() {
        TavernDefinition definition = new TavernDefinition("bank", "bank.ioza.dev",
                "/**", "/bank", List.of(), List.of(), List.of(), true);
        assertEquals(List.of(TavernAccess.KNIGHT), definition.allowedAccess());
    }

    @Test
    void canOptIntoCarriageForTower() {
        TavernDefinition definition = new TavernDefinition("tower", "tower.ioza.dev",
                "/**", "/tower", List.of(TavernAccess.KNIGHT, TavernAccess.CARRIAGE),
                List.of(), List.of(), true);
        assertTrue(definition.allowedAccess().contains(TavernAccess.CARRIAGE));
    }

    @Test
    void rejectsReservedBastionDomain() {
        assertThrows(IllegalArgumentException.class,
                () -> new TavernDefinition("bad", "bastion.ioza.dev",
                        "/**", "/bad", null, null, null, true));
    }
}
