package ch.portami.inventorybackend.felt.exception;

import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;
import java.text.MessageFormat;

/**
 * Thrown when a felt is looked up by an id that does not correspond to an existing felt.
 */
public class FeltNotFoundException extends ResourceNotFoundException {

    public FeltNotFoundException(long feltId) {
        super(MessageFormat.format("Felt with id {0} not found", feltId), new ResourceIdentifier("feltId", feltId));
    }
}
