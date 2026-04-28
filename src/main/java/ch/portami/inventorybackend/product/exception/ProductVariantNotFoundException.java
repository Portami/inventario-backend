package ch.portami.inventorybackend.product.exception;

import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;

public class ProductVariantNotFoundException extends ResourceNotFoundException {

    public ProductVariantNotFoundException(long variantId) {
        super("Product variant with id %d not found".formatted(variantId));
    }

    public ProductVariantNotFoundException(long productId, long variantId) {
        super("Product variant with id %d not found for product with id %d".formatted(variantId, productId));
    }

}
