package ch.portami.inventorybackend.felt.exception;

import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;
import java.text.MessageFormat;

public class FeltRollNotFoundException extends ResourceNotFoundException {

    public FeltRollNotFoundException(long rollId) {
        super(MessageFormat.format("Roll with id {0} not found", rollId), new ResourceIdentifier("rollId", rollId));
    }
}
