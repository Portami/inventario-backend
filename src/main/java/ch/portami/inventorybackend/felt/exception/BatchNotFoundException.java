package ch.portami.inventorybackend.felt.exception;

import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;
import java.text.MessageFormat;

/**
 * Thrown when a batch is looked up by an id that does not correspond to an existing batch.
 */
public class BatchNotFoundException extends ResourceNotFoundException {

    public BatchNotFoundException(long batchId) {
        super(MessageFormat.format("Batch with id {0} not found", batchId), new ResourceIdentifier("batchId", batchId));
    }
}
