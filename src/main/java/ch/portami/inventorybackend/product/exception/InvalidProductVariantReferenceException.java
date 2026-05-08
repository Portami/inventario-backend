package ch.portami.inventorybackend.product.exception;

import ch.portami.inventorybackend.core.exceptions.InvalidResourceReferenceException;
import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import java.text.MessageFormat;

public class InvalidProductVariantReferenceException extends InvalidResourceReferenceException {

    public InvalidProductVariantReferenceException(Long variantId) {
        super(MessageFormat.format("The request is referencing a product variant with id {0} that does not exist", variantId), new ResourceIdentifier("variantId", variantId));
    }

}
