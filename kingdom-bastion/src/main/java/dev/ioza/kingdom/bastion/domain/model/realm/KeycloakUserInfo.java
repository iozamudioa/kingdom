package dev.ioza.kingdom.bastion.domain.model.realm;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KeycloakUserInfo(String sub,

        @JsonProperty("preferred_username") String preferredUsername,

        String email,

        @JsonProperty("given_name") String givenName,

        @JsonProperty("family_name") String familyName) {

}
