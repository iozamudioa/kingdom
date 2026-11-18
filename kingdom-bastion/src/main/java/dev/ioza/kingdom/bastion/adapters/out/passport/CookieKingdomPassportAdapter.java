package dev.ioza.kingdom.bastion.adapters.out.passport;

import java.time.Duration;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import dev.ioza.kingdom.bastion.domain.ports.out.KingdomPassportPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CookieKingdomPassportAdapter implements KingdomPassportPort {

    private final KingdomPassportProperties properties;

    @Override
    public ResponseCookie create(String sessionId) {
        return builder(sessionId).build();
    }

    @Override
    public ResponseCookie destroy() {
        log.info("Destruyendo cookie");
        return builder("").maxAge(Duration.ZERO).build();
    }

    private ResponseCookie.ResponseCookieBuilder builder(String value) {

        Cookie cookie = properties.cookie();

        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(cookie.name(), value)
                .path("/").httpOnly(true).secure(cookie.secure()).sameSite(cookie.sameSite());

        if (StringUtils.hasText(cookie.domain())) {
            builder.domain(cookie.domain());
        }

        return builder;
    }

}
