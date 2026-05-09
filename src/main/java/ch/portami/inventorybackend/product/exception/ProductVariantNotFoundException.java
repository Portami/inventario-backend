package ch.portami.inventorybackend.product.exception;

import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;
import java.text.MessageFormat;

public class ProductVariantNotFoundException extends ResourceNotFoundException {

    public ProductVariantNotFoundException(long productId, long variantId) {
        super(MessageFormat.format("Product variant with id {1} not found for product with id {0}", productId, variantId),
                new ResourceIdentifier("productId", productId),
                new ResourceIdentifier("variantId", variantId));
    }

}
