package dev.ioza.kingdom.tavern.host.web;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Same-origin API proxy: browser -> Tavern Host -> Bastion -> API Tavern.
 * The incoming Host is preserved so Bastion resolves the API Tavern, not this UI Tavern.
 */
@RestController
@ConditionalOnProperty(prefix = "kingdom.tavern.host", name = "api-proxy-url")
public class ApiProxyController {

    private static final Set<String> HOP_BY_HOP = Set.of("connection", "keep-alive",
            "proxy-authenticate", "proxy-authorization", "te", "trailer",
            "transfer-encoding", "upgrade", "host", "content-length");

    private final URI bastion;
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NEVER).build();

    public ApiProxyController(@Value("${kingdom.tavern.host.api-proxy-url}") String url) {
        this.bastion = URI.create(url.endsWith("/") ? url.substring(0, url.length() - 1) : url);
        if (!Set.of("http", "https").contains(bastion.getScheme())
                || bastion.getHost() == null || bastion.getRawQuery() != null
                || bastion.getRawFragment() != null || !bastion.getPath().isEmpty()) {
            throw new IllegalArgumentException("API proxy URL must be an HTTP(S) origin");
        }
    }

    @RequestMapping({"/api", "/api/**"})
    public ResponseEntity<byte[]> proxy(HttpServletRequest request) throws IOException, InterruptedException {
        String path = request.getRequestURI();
        String query = request.getQueryString();
        URI target = URI.create(bastion + path + (query == null ? "" : "?" + query));
        byte[] body = request.getInputStream().readAllBytes();
        HttpRequest.Builder outgoing = HttpRequest.newBuilder(target)
                .timeout(Duration.ofSeconds(60))
                .method(request.getMethod(), HttpRequest.BodyPublishers.ofByteArray(body));

        var names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (HOP_BY_HOP.contains(name.toLowerCase(Locale.ROOT))
                    || name.toLowerCase(Locale.ROOT).startsWith("x-forwarded-")) {
                continue;
            }
            var values = request.getHeaders(name);
            while (values.hasMoreElements()) {
                outgoing.header(name, values.nextElement());
            }
        }
        // Java HttpClient controls the Host header; Bastion must see the public domain.
        outgoing.header("Host", request.getServerName());
        outgoing.header("X-Forwarded-Host", request.getServerName());
        outgoing.header("X-Forwarded-Proto", request.getHeader("X-Forwarded-Proto") == null
                ? request.getScheme() : request.getHeader("X-Forwarded-Proto"));

        HttpResponse<byte[]> response = client.send(outgoing.build(),
                HttpResponse.BodyHandlers.ofByteArray());
        HttpHeaders headers = new HttpHeaders();
        response.headers().map().forEach((name, values) -> {
            if (!HOP_BY_HOP.contains(name.toLowerCase(Locale.ROOT))) {
                headers.addAll(name, values);
            }
        });
        return ResponseEntity.status(response.statusCode()).headers(headers).body(response.body());
    }
}
