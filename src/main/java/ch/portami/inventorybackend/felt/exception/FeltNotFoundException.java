package ch.portami.inventorybackend.felt.exception;

import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;

public class FeltNotFoundException extends ResourceNotFoundException {

    public FeltNotFoundException(long feltId) {
        super("Felt with id %d not found".formatted(feltId));
    }

}
