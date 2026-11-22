package dev.ioza.kingdom.bastion.adapters.out.realm;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kingdom.realm.keycloak")
public record KeycloakRealmProperties(

        String baseUrl,

        String realm,

        ClientProperties herald) {

    public KeycloakRealmProperties {

        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("Keycloak base URL is required");
        }

        if (realm == null || realm.isBlank()) {
            throw new IllegalArgumentException("Keycloak realm is required");
        }

        if (herald == null) {
            throw new IllegalArgumentException("Keycloak herald configuration is required");
        }
    }

    public String issuerUri() {
        return "%s/realms/%s".formatted(removeTrailingSlash(baseUrl), realm);
    }

    public String authorizationUri() {
        return issuerUri() + "/protocol/openid-connect/auth";
    }

    public String tokenUri() {
        return issuerUri() + "/protocol/openid-connect/token";
    }

    public String logoutUri() {
        return issuerUri() + "/protocol/openid-connect/logout";
    }

    public String jwkSetUri() {
        return issuerUri() + "/protocol/openid-connect/certs";
    }

    private static String removeTrailingSlash(String value) {
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
