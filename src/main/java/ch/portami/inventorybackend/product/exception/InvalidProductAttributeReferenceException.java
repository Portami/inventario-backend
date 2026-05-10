package ch.portami.inventorybackend.product.exception;

import ch.portami.inventorybackend.core.exceptions.InvalidResourceReferenceException;
import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import java.text.MessageFormat;

public class InvalidProductAttributeReferenceException extends InvalidResourceReferenceException {

    public InvalidProductAttributeReferenceException(Long productId, Long attributeId) {
        super(MessageFormat.format(
                        "The request is referencing a product attribute with id {1} that does not exist for the product with id {0}",
                        productId, attributeId),
                new ResourceIdentifier("productId", productId),
                new ResourceIdentifier("attributeId", attributeId));
    }
}
