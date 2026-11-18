package dev.ioza.kingdom.bastion.application.exile;

import java.net.URI;
import org.springframework.http.ResponseCookie;

public record ExileResult(URI returnTo, ResponseCookie cookie) {

}
