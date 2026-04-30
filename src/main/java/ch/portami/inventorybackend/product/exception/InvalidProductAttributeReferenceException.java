package ch.portami.inventorybackend.product.exception;

import ch.portami.inventorybackend.core.exceptions.InvalidResourceReferenceException;

public class InvalidProductAttributeReferenceException extends InvalidResourceReferenceException {

    public InvalidProductAttributeReferenceException(long productId, long attributeId) {
        super("The request is referencing a product attribute with id %d that does not exist for the product with id %d".formatted(
                attributeId, productId));
    }
}
