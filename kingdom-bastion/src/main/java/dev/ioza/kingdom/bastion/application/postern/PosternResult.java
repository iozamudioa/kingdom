package dev.ioza.kingdom.bastion.application.postern;

import java.net.URI;
import org.springframework.http.ResponseCookie;

public record PosternResult(URI returnTo, ResponseCookie cookie) {

}
