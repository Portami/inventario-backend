package ch.portami.inventorybackend.felt.exception;

import ch.portami.inventorybackend.core.exceptions.InvalidResourceReferenceException;
import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import java.text.MessageFormat;

/**
 * Thrown when a request references a felt type by an id that does not correspond to an existing
 * felt type.
 */
public class InvalidFeltTypeReferenceException extends InvalidResourceReferenceException {

    public InvalidFeltTypeReferenceException(long feltTypeId) {
        super(MessageFormat.format("The request is referencing a felt type with id {0} that does not exist",
                feltTypeId), new ResourceIdentifier("feltTypeId", feltTypeId));
    }

}
