package ch.portami.inventorybackend.product.exception;

import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;

public class ProductAttributeNotFound extends ResourceNotFoundException {

    public ProductAttributeNotFound(long productId, long attributeId) {
        super("Product attribute with id %d not found for product with id %d".formatted(attributeId, productId));
    }
}
