package ch.portami.inventorybackend.felt.exception;

import ch.portami.inventorybackend.core.exceptions.InvalidResourceReferenceException;
import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import java.text.MessageFormat;

public class InvalidFeltTypeReferenceException extends InvalidResourceReferenceException {

    public InvalidFeltTypeReferenceException(long feltTypeId) {
        super(MessageFormat.format("The request is referencing a felt type with id {0} that does not exist", feltTypeId), new ResourceIdentifier("feltTypeId", feltTypeId));
    }

}
