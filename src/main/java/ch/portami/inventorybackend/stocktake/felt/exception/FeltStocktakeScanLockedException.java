package ch.portami.inventorybackend.stocktake.felt.exception;

import ch.portami.inventorybackend.core.exceptions.BusinessRuleViolationException;
import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import java.text.MessageFormat;

public class FeltStocktakeScanLockedException extends BusinessRuleViolationException {

    public FeltStocktakeScanLockedException(Long stocktakeId, Long scanId) {
        super(MessageFormat.format(
                        "Scan with id {0} in stocktake with id {1} cannot be voided because it is involved in a problem reolution. Please unresolve the problem first.",
                        scanId, stocktakeId),
                new ResourceIdentifier("stocktakeId", stocktakeId),
                new ResourceIdentifier("scanId", scanId));
    }
    
}
