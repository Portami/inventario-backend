package ch.portami.inventorybackend.felt.exception;

import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;
import java.text.MessageFormat;

public class BatchNotFoundException extends ResourceNotFoundException {

    public BatchNotFoundException(long batchId) {
        super(MessageFormat.format("Batch with id {0} not found", batchId), "batchId", batchId);
    }
}
