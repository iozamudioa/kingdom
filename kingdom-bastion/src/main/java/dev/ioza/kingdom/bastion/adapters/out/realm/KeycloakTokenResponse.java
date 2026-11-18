package dev.ioza.kingdom.bastion.adapters.out.realm;

import com.fasterxml.jackson.annotation.JsonProperty;

record KeycloakTokenResponse(


        @JsonProperty("access_token") String accessToken,

        @JsonProperty("expires_in") long expiresIn,

        @JsonProperty("refresh_token") String refreshToken,

        @JsonProperty("refresh_expires_in") long refreshExpiresIn,

        @JsonProperty("token_type") String tokenType,

        @JsonProperty("id_token") String idToken,

        String scope) {

}
