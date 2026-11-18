package dev.ioza.kingdom.bastion.domain.model.realm;

public class InvalidAudienceStateException extends RuntimeException {

    public InvalidAudienceStateException() {
        super("Invalid audience state.");
    }

}
