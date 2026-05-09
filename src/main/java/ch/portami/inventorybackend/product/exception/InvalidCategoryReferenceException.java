package ch.portami.inventorybackend.product.exception;

import ch.portami.inventorybackend.core.exceptions.InvalidResourceReferenceException;
import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import java.text.MessageFormat;

public class InvalidCategoryReferenceException extends InvalidResourceReferenceException {

    public InvalidCategoryReferenceException(long categoryId) {
        super(MessageFormat.format("The request is referencing a category with id {0} that does not exist", categoryId), new ResourceIdentifier("categoryId", categoryId));
    }
    
}
