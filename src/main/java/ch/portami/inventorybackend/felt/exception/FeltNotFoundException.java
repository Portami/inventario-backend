package ch.portami.inventorybackend.felt.exception;

import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;
import java.text.MessageFormat;

public class FeltNotFoundException extends ResourceNotFoundException {

    public FeltNotFoundException(long feltId) {
        super(MessageFormat.format("Felt with id {0} not found", feltId), new ResourceIdentifier("feltId", feltId));
    }
}
