package dev.ioza.kingdom.bastion.domain.model.realm;

public class RealmUnavailableException extends RuntimeException {

    public RealmUnavailableException(Throwable cause) {
        super("Realm is unavailable", cause);
    }

    public RealmUnavailableException(String message) {
        super(message);
    }

}
