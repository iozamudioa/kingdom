package dev.ioza.kingdom.bastion.adapters.in.rest.seal;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.ioza.kingdom.bastion.application.seal.SealResult;

public record SealResponse(

        @JsonProperty("access_token") String accessToken,

        @JsonProperty("token_type") String tokenType,

        @JsonProperty("expires_in") long expiresIn


) {

    public static SealResponse from(SealResult result) {
        return new SealResponse(result.accessToken(), result.tokenType(), result.expiresIn());
    }

}
