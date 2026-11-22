package dev.ioza.kingdom.bastion.adapters.in.gateway.authentication;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.pattern.PathPatternParser;
import dev.ioza.kingdom.bastion.adapters.in.gateway.TavernAuthenticator;
import dev.ioza.kingdom.bastion.adapters.out.registry.TavernResolution;
import dev.ioza.kingdom.bastion.adapters.out.sentinel.JwtSentinelProperties;
import dev.ioza.kingdom.bastion.domain.ports.out.KingdomPassportPort;
import dev.ioza.kingdom.bastion.domain.ports.out.TavernRegistryPort;
import dev.ioza.kingdom.contracts.TavernManifest;
import dev.ioza.kingdom.contracts.TavernRoutePredicate;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class RoyalGuardFilter implements WebFilter {

    private final TavernRegistryPort tavernRegistry;
    private final List<TavernAuthenticator> authenticatorsList;

    private final KingdomPassportPort kingdomPassportPort;
    private final JwtSentinelProperties jwtSentinelProperties;
    private final PathPatternParser pathPatternParser;

    public RoyalGuardFilter(TavernRegistryPort tavernRegistry,
            List<TavernAuthenticator> authenticators, KingdomPassportPort kingdomPassportPort,
            JwtSentinelProperties jwtSentinelProperties) {
        this.authenticatorsList = List.copyOf(authenticators);
        this.tavernRegistry = tavernRegistry;
        this.kingdomPassportPort = kingdomPassportPort;
        this.jwtSentinelProperties = jwtSentinelProperties;
        this.pathPatternParser = new PathPatternParser();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String host = exchange.getRequest().getHeaders().getFirst(HttpHeaders.HOST);

        String path = exchange.getRequest().getPath().value();

        // CORS belongs at the gateway: even a 401 must be readable by the SPA.
        // Only BANK's trusted frontend may make credentialed cross-origin API calls.
        String origin = exchange.getRequest().getHeaders().getOrigin();
        boolean bankApi = "bank-api.ioza.dev".equalsIgnoreCase(normalizeHost(host == null ? "" : host))
                && path.startsWith("/api/v1/");
        boolean trustedOrigin = "https://bank.ioza.dev".equals(origin);
        if (bankApi && trustedOrigin) {
            HttpHeaders responseHeaders = exchange.getResponse().getHeaders();
            responseHeaders.setAccessControlAllowOrigin(origin);
            responseHeaders.setAccessControlAllowCredentials(true);
            responseHeaders.add(HttpHeaders.VARY, HttpHeaders.ORIGIN);
            if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS
                    && exchange.getRequest().getHeaders()
                            .containsKey(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD)) {
                String requestedMethod = exchange.getRequest().getHeaders()
                        .getFirst(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD);
                if (!List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .contains(requestedMethod)) {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }
                responseHeaders.setAccessControlAllowMethods(
                        List.of(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT,
                                HttpMethod.PATCH, HttpMethod.DELETE, HttpMethod.OPTIONS));
                responseHeaders.setAccessControlAllowHeaders(
                        List.of("Authorization", "Content-Type", "Accept", "X-Requested-With"));
                responseHeaders.setAccessControlMaxAge(3600);
                exchange.getResponse().setStatusCode(HttpStatus.NO_CONTENT);
                return exchange.getResponse().setComplete();
            }
        }

        Optional<TavernResolution> resolution = tavernRegistry.resolve(host, path);


        if (resolution.isEmpty()) {

            log.info("Tavern not found for host: {} and path: {}", host, path);

            return chain.filter(exchange);
        }

        TavernManifest tavern = resolution.get().tavern();

        exchange.getAttributes().put(KingdomRoutingFilter.KINGDOM_RESOLUTION_ATTRIBUTE,
                resolution.get());

        if (requiresTrailingSlash(exchange, tavern)) {

            log.info("Redirecting to trailing slash for host: {} and path: {}", host, path);

            return redirectToTrailingSlash(exchange, chain);
        }

        if (isPublicPath(exchange, tavern)) {
            return chain.filter(exchange);
        }

        authenticatorsList.forEach(authenticator -> log.debug("Authenticator: {}",
                authenticator.getClass().getSimpleName()));

        return authenticate(tavern, authenticatorsList.iterator(), exchange, chain);
    }



    private boolean isPublicPath(ServerWebExchange exchange, TavernManifest tavern) {

        String path = normalizePath(exchange.getRequest().getPath().value(), tavern);

        log.info("Normalized path: {}", path);

        boolean result = tavern.publicPaths().stream().map(pathPatternParser::parse)
                .anyMatch(pattern -> pattern.matches(PathContainer.parsePath(path)));

        log.info("isPublicPath: {}", result);

        return result;
    }

    private String normalizePath(String path, TavernManifest tavern) {

        String context = tavern.routePredicates().stream().filter(p -> "Path".equals(p.name()))
                .findFirst().map(p -> p.value().replace("/**", "")).orElse("");

        if (!context.isBlank() && path.startsWith(context)) {
            return path.substring(context.length());
        }

        return path;
    }


    private boolean isStaticResource(ServerWebExchange exchange) {

        String path = exchange.getRequest().getPath().value();

        boolean result = path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".png")
                || path.endsWith(".jpg") || path.endsWith(".svg") || path.endsWith(".ico")
                || path.startsWith("/assets/");

        log.info("isStaticResource: {}", result);

        return result;
    }

    private boolean requiresTrailingSlash(ServerWebExchange exchange, TavernManifest tavern) {

        if (!tavern.capabilities().contains("ui")) {
            return false;
        }

        String host = exchange.getRequest().getHeaders().getHost().getHostString();
        String path = exchange.getRequest().getPath().value();

        boolean contextRequest = path.equals(contextRoot(tavern));
        boolean domainRequest =
                normalizeHost(host).equalsIgnoreCase(domain(tavern)) && "/".equals(path);

        return contextRequest || domainRequest;
    }

    private String contextRoot(TavernManifest tavern) {
        return tavern.routePredicates().stream().filter(p -> "Path".equals(p.name())).findFirst()
                .map(p -> p.value().replace("/**", "")).orElse("/");
    }

    private String domain(TavernManifest tavern) {
        return tavern.routePredicates().stream().filter(p -> "Host".equals(p.name())).findFirst()
                .map(TavernRoutePredicate::value).orElse("");
    }

    private String normalizeHost(String host) {
        int index = host.indexOf(':');
        return index >= 0 ? host.substring(0, index) : host;
    }

    private Mono<Void> redirectToTrailingSlash(ServerWebExchange exchange, WebFilterChain chain) {

        URI uri = exchange.getRequest().getURI();

        if (uri.getPath().endsWith("/")) {
            return chain.filter(exchange);
        }

        URI location = UriComponentsBuilder.fromUri(uri).replacePath(uri.getPath() + "/")
                .build(true).toUri();

        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
        exchange.getResponse().getHeaders().setLocation(location);

        return exchange.getResponse().setComplete();
    }

    private Mono<Void> authenticate(TavernManifest tavern, Iterator<TavernAuthenticator> iterator,
            ServerWebExchange exchange, WebFilterChain chain) {

        if (!iterator.hasNext()) {
            return unauthorized(exchange);
        }

        TavernAuthenticator authenticator = iterator.next();

        if (authenticator == null) {
            log.warn("No authenticator found. Skipping to next.");
            return authenticate(tavern, iterator, exchange, chain);
        }

        log.info("Trying auth with : {}", authenticator);

        return authenticator.authenticate(exchange, tavern)
                .flatMap(result -> switch (result.status()) {
                    case AUTHENTICATED -> {
                        log.debug("Authenticated request [{}] {}",
                                exchange.getRequest().getMethod(), exchange.getRequest().getURI());
                        yield chain.filter(result.exchange());
                    }
                    case NOT_AUTHENTICATED -> authenticate(tavern, iterator, exchange, chain);
                });
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {

        if (requiresInteractiveAuthentication(exchange)) {
            return redirectToAudience(exchange);
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

        return exchange.getResponse().setComplete();
    }

    private Mono<Void> redirectToAudience(ServerWebExchange exchange) {

        String returnTo =
                UriUtils.encode(exchange.getRequest().getURI().toString(), StandardCharsets.UTF_8);

        URI audience =
                URI.create(jwtSentinelProperties.issuer() + "/audience?returnTo=" + returnTo);

        ResponseCookie passport = kingdomPassportPort.destroy();

        exchange.getResponse().addCookie(passport);
        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
        exchange.getResponse().getHeaders().setLocation(audience);

        return exchange.getResponse().setComplete();
    }

    private boolean requiresInteractiveAuthentication(ServerWebExchange exchange) {

        HttpHeaders headers = exchange.getRequest().getHeaders();

        String fetchMode = headers.getFirst("Sec-Fetch-Mode");
        String fetchDest = headers.getFirst("Sec-Fetch-Dest");

        if ("navigate".equalsIgnoreCase(fetchMode)) {
            return true;
        }

        if ("document".equalsIgnoreCase(fetchDest)) {
            return true;
        }

        return headers.getAccept().stream()
                .anyMatch(type -> type.includes(org.springframework.http.MediaType.TEXT_HTML));
    }

}
