package ch.portami.inventorybackend.product.exception;

import ch.portami.inventorybackend.core.exceptions.BusinessRuleViolationException;
import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import java.text.MessageFormat;

public class NotEnoughInventoryException extends BusinessRuleViolationException {

    public NotEnoughInventoryException(Long productVariantId, Long storageId, int requestedQuantityReduction,
            int currentQuantity) {
        super(MessageFormat.format(
                        "Cannot reduce inventory for product variant with id {0} in storage with id {1} by {2}. Only {3} item(s) present.",
                        productVariantId, storageId, requestedQuantityReduction, currentQuantity),
                new ResourceIdentifier("productVariantId", productVariantId),
                new ResourceIdentifier("storageId", storageId),
                new ResourceIdentifier("requestedQuantityReduction", requestedQuantityReduction),
                new ResourceIdentifier("currentQuantity", currentQuantity));
    }

    public NotEnoughInventoryException(Long productVariantId, Long storageId, int requestedQuantityReduction) {
        super(MessageFormat.format(
                        "Cannot reduce inventory for product variant with id {0} in storage with id {1} by {2}. There are no items present.",
                        productVariantId, storageId, requestedQuantityReduction),
                new ResourceIdentifier("productVariantId", productVariantId),
                new ResourceIdentifier("storageId", storageId),
                new ResourceIdentifier("requestedQuantityReduction", requestedQuantityReduction));
    }

}
