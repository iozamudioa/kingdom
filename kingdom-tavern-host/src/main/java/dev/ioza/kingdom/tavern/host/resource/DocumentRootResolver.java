package dev.ioza.kingdom.tavern.host.resource;

import java.nio.file.Path;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import dev.ioza.kingdom.tavern.host.configuration.TavernHostProperties;

@Component
public class DocumentRootResolver {

    private final TavernHostProperties properties;

    public DocumentRootResolver(TavernHostProperties properties) {
        this.properties = properties;
    }

    public Resource resolve(String file) {

        try {

            Path path = Path.of(properties.source(), file);

            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists()) {
                throw new DocumentRootException(file, null);
            }

            return resource;

        } catch (Exception ex) {
            throw new DocumentRootException(file, ex);
        }

    }

}
