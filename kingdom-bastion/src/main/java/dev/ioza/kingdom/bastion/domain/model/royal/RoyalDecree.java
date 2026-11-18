package dev.ioza.kingdom.bastion.domain.model.royal;

public record RoyalDecree(

        String authority

) {

    public RoyalDecree {

        if (authority == null || authority.isBlank()) {
            throw new IllegalArgumentException("RoyalDecree authority cannot be null or blank");
        }

    }

}
