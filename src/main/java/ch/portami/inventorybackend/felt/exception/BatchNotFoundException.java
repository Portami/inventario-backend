package ch.portami.inventorybackend.felt.exception;

import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;

public class BatchNotFoundException extends ResourceNotFoundException {

    public BatchNotFoundException(long batchId) {
        super("Batch with id %d not found".formatted(batchId));
    }

}
