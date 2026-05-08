package ch.portami.inventorybackend.product.exception;

import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;
import java.text.MessageFormat;

public class CategoryNotFoundException extends ResourceNotFoundException {

    public CategoryNotFoundException(long categoryId) {
        super(MessageFormat.format("Category with id {0} not found", categoryId), "categoryId", categoryId);
    }
}
