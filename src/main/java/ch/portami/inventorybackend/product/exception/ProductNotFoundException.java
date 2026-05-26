package ch.portami.inventorybackend.product.exception;

import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;
import java.text.MessageFormat;

public class ProductNotFoundException extends ResourceNotFoundException {

    public ProductNotFoundException(Long productId) {
        super(MessageFormat.format("Product with id {0} not found", productId),
                new ResourceIdentifier("productId", productId));
    }

}
