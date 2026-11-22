package dev.ioza.kingdom.contracts.identity;
public class IdentityNotFoundException extends RuntimeException {
    public IdentityNotFoundException() { super("No authenticated Kingdom identity"); }
}
