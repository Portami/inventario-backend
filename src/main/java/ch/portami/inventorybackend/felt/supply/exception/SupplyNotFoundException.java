package ch.portami.inventorybackend.felt.supply.exception;

import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;
import java.text.MessageFormat;

public class SupplyNotFoundException extends ResourceNotFoundException {

    public SupplyNotFoundException(long feltColorVariantId) {
        super(MessageFormat.format("Supply with id {0} not found", feltColorVariantId), new ResourceIdentifier("feltColorVariantId", feltColorVariantId));
    }
}
