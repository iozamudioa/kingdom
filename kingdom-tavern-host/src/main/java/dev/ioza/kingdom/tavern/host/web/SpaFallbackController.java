package dev.ioza.kingdom.tavern.host.web;

import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import dev.ioza.kingdom.tavern.host.resource.DocumentRootResolver;

@Controller
public class SpaFallbackController {


    private final DocumentRootResolver resolver;

    public SpaFallbackController(DocumentRootResolver resolver) {
        this.resolver = resolver;
    }

    @GetMapping("/")
    public ResponseEntity<Resource> index() throws IOException {

        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML)
                .body(resolver.resolve("index.html"));

    }

}
