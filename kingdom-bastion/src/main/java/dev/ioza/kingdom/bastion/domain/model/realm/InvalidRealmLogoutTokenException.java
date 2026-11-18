package dev.ioza.kingdom.bastion.domain.model.realm;

public class InvalidRealmLogoutTokenException extends RuntimeException {

    public InvalidRealmLogoutTokenException(String message) {
        super(message);
    }

    public InvalidRealmLogoutTokenException(String message, Throwable cause) {

        super(message, cause);
    }
}
