package ch.portami.inventorybackend.product.exception;

import ch.portami.inventorybackend.core.exceptions.InvalidResourceReferenceException;

public class InvalidCategoryReferenceException extends InvalidResourceReferenceException {

    public InvalidCategoryReferenceException(long categoryId) {
        super("The request is referencing a category with id %d that does not exist".formatted(categoryId));
    }
    
}
