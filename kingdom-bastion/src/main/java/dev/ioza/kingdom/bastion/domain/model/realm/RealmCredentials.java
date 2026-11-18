package dev.ioza.kingdom.bastion.domain.model.realm;

public record RealmCredentials(

        String id,

        String clientSecret) {

    public RealmCredentials {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Realm id cannot be null or blank");
        }

        if (clientSecret == null || clientSecret.isBlank()) {
            throw new IllegalArgumentException("Realm client secret cannot be null or blank");
        }

    }

}
