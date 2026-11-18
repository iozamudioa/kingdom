package dev.ioza.kingdom.bastion.application.audience.callback;

import java.net.URI;
import org.springframework.http.ResponseCookie;

public record AudienceCallbackResult(URI returnTo, ResponseCookie cookie) {

}
