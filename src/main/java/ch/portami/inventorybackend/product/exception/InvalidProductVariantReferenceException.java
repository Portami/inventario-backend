package ch.portami.inventorybackend.product.exception;

import ch.portami.inventorybackend.core.exceptions.InvalidResourceReferenceException;

public class InvalidProductVariantReferenceException extends InvalidResourceReferenceException {

    public InvalidProductVariantReferenceException(Long variantId) {
        super("The request is referencing a product variant with id %d that does not exist".formatted(variantId));
    }

}
