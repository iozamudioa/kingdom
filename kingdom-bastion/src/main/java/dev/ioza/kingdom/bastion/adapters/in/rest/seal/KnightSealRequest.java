package dev.ioza.kingdom.bastion.adapters.in.rest.seal;
import jakarta.validation.constraints.NotBlank;
public record KnightSealRequest(@NotBlank String username, @NotBlank String password) {
    @Override public String toString() { return "KnightSealRequest[credentials=REDACTED]"; }
}
