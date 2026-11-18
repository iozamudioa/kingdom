package dev.ioza.kingdom.bastion.domain.model.postern;

public class InvalidCarriageException extends RuntimeException {

    public InvalidCarriageException() {
        super("Invalid carriage.");
    }
}
