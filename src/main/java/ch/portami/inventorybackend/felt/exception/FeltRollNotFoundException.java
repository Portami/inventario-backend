package ch.portami.inventorybackend.felt.exception;

import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;

public class FeltRollNotFoundException extends ResourceNotFoundException {

    public FeltRollNotFoundException(long rollId) {
        super("Roll with id %d not found".formatted(rollId));
    }

}
