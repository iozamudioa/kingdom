package dev.ioza.kingdom.bastion.domain.model.realm;

public class InvalidRealmCredentialsException extends RuntimeException {

    public InvalidRealmCredentialsException() {
        super("Invalid realm credentials");
    }

    public InvalidRealmCredentialsException(Throwable cause) {
        super("Invalid realm credentials", cause);
    }

}
