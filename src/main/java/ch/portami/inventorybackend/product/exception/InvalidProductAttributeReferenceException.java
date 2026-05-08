package ch.portami.inventorybackend.product.exception;

import ch.portami.inventorybackend.core.exceptions.InvalidResourceReferenceException;
import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;

public class InvalidProductAttributeReferenceException extends InvalidResourceReferenceException {

    public InvalidProductAttributeReferenceException(long productId, long attributeId) {
        super("The request is referencing a product attribute with id %d that does not exist for the product with id %d".formatted(
                attributeId, productId),
                new ResourceIdentifier("productId", productId),
                new ResourceIdentifier("attributeId", attributeId));
    }
}
