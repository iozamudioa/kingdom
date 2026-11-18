package dev.ioza.kingdom.tavern.host.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClient;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@ConditionalOnProperty(prefix = "kingdom.tavern.host", name = "mode", havingValue = "PROXY")
public class ReverseProxyController {

    private final RestClient restClient;

    public ReverseProxyController(RestClient restClient) {
        this.restClient = restClient;
    }

    @RequestMapping("/**")
    public ResponseEntity<byte[]> proxy(HttpServletRequest request) {

        String path = request.getRequestURI();

        if (request.getQueryString() != null) {
            path += "?" + request.getQueryString();
        }

        return restClient.method(org.springframework.http.HttpMethod.valueOf(request.getMethod()))
                .uri(path).retrieve().toEntity(byte[].class);

    }

}
