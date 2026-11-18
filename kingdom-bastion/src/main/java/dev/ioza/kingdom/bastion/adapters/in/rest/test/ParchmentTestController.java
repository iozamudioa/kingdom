package dev.ioza.kingdom.bastion.adapters.in.rest.test;

import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ParchmentTestController {

    @GetMapping("/parchment/test")
    public Map<String, Object> test(@AuthenticationPrincipal Jwt parchment) {

        return Map.of(

                "subject", parchment.getSubject(),

                "identityType", parchment.getClaimAsString("identity_type"),

                "roles", parchment.getClaimAsStringList("roles"),

                "decrees", parchment.getClaimAsStringList("decrees"),

                "issuer", parchment.getIssuer().toString());
    }

    @GetMapping("/test")
    public Map<String, Object> test() {

        return Map.of("test", "ok");
    }
}
