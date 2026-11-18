package dev.ioza.kingdom.bastion.adapters.in.rest.archives;

import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class RoyalArchivesController {

    private final RSAKey bastionRsaKey;

    @GetMapping(value = "/royal-archives", produces = MediaType.APPLICATION_JSON_VALUE)
    public RoyalArchivesResponse royalArchives() {

        Map<String, Object> publicRecord = bastionRsaKey.toPublicJWK().toJSONObject();

        return new RoyalArchivesResponse(List.of(publicRecord));
    }

}
