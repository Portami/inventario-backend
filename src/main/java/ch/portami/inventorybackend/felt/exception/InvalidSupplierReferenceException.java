package ch.portami.inventorybackend.felt.exception;

import ch.portami.inventorybackend.core.exceptions.InvalidResourceReferenceException;
import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import java.text.MessageFormat;

/**
 * Thrown when a request references a supplier by an id that does not correspond to an existing
 * supplier.
 */
public class InvalidSupplierReferenceException extends InvalidResourceReferenceException {

    public InvalidSupplierReferenceException(long supplierId) {
        super(MessageFormat.format("The request is referencing a supplier with id {0} that does not exist", supplierId),
                new ResourceIdentifier("supplierId", supplierId));
    }

}
