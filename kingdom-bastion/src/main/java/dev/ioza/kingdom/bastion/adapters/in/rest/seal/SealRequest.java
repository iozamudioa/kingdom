package dev.ioza.kingdom.bastion.adapters.in.rest.seal;

import jakarta.validation.constraints.NotBlank;

public record SealRequest(

        @NotBlank(message = "clientId is required") String clientId,

        @NotBlank(message = "clientSecret is required") String clientSecret

) {

}
