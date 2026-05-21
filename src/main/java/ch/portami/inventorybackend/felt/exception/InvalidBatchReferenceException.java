package ch.portami.inventorybackend.felt.exception;

import ch.portami.inventorybackend.core.exceptions.InvalidResourceReferenceException;
import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import java.text.MessageFormat;

public class InvalidBatchReferenceException extends InvalidResourceReferenceException {

    public InvalidBatchReferenceException(long batchId) {
        super(MessageFormat.format("The request is referencing a batch with id {0} that does not exist", batchId),
                new ResourceIdentifier("batchId", batchId));
    }

}
