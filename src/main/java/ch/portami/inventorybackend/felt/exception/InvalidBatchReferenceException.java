package ch.portami.inventorybackend.felt.exception;

import ch.portami.inventorybackend.core.exceptions.InvalidResourceReferenceException;

public class InvalidBatchReferenceException extends InvalidResourceReferenceException {

    public InvalidBatchReferenceException(long batchId) {
        super("The request is referencing a batch with id %d that does not exist".formatted(batchId));
    }

}
