package dev.ioza.kingdom.bastion.adapters.out.registry;

import dev.ioza.kingdom.contracts.TavernManifest;

public record TavernResolution(TavernManifest tavern, String forwardedPath) {
}
