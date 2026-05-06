package ch.portami.inventorybackend.product.exception;

import ch.portami.inventorybackend.core.exceptions.BusinessRuleViolationException;

public class NotEnoughInventoryException extends BusinessRuleViolationException {

    public NotEnoughInventoryException(long productVariantId, long storageId, int requestedQuantityReduction,
            int currentQuantity) {
        super("Cannot reduce inventory for product variant with id %d in storage with id %d by %d. Only %d item(s) present.".formatted(
                productVariantId, storageId, requestedQuantityReduction, currentQuantity));
    }

    public NotEnoughInventoryException(long productVariantId, long storageId, int requestedQuantityReduction) {
        super("Cannot reduce inventory for product variant with id %d in storage with id %d by %d. There are no items present.".formatted(
                productVariantId, storageId, requestedQuantityReduction));
    }

}
