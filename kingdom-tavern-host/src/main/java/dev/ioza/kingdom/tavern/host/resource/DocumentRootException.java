package dev.ioza.kingdom.tavern.host.resource;

public class DocumentRootException extends RuntimeException {

    public DocumentRootException(String location, Throwable cause) {
        super("Unable to resolve resource: " + location, cause);
    }

}
