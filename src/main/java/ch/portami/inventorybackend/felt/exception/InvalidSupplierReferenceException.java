package ch.portami.inventorybackend.felt.exception;

import ch.portami.inventorybackend.core.exceptions.InvalidResourceReferenceException;

public class InvalidSupplierReferenceException extends InvalidResourceReferenceException {

    public InvalidSupplierReferenceException(long supplierId) {
        super("The request is referencing a supplier with id %d that does not exist".formatted(supplierId));
    }

}
