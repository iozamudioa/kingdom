package dev.ioza.kingdom.bastion.application.seal;

public record SealCommand(

        String clientId, String clientSecret

) {

    public SealCommand {

        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException("SealCommand clientId cannot be null or blank");
        }

        if (clientSecret == null || clientSecret.isBlank()) {
            throw new IllegalArgumentException("SealCommand clientSecret cannot be null or blank");
        }

    }

}
