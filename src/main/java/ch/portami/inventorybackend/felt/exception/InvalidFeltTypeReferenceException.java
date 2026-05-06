package ch.portami.inventorybackend.felt.exception;

import ch.portami.inventorybackend.core.exceptions.InvalidResourceReferenceException;

public class InvalidFeltTypeReferenceException extends InvalidResourceReferenceException {

    public InvalidFeltTypeReferenceException(long feltTypeId) {
        super("The request is referencing a felt type with id %d that does not exist".formatted(feltTypeId));
    }

}
