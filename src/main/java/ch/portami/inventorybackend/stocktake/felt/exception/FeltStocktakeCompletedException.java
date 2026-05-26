package ch.portami.inventorybackend.stocktake.felt.exception;

import ch.portami.inventorybackend.core.exceptions.BusinessRuleViolationException;
import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import java.text.MessageFormat;

public class FeltStocktakeCompletedException extends BusinessRuleViolationException {

    public FeltStocktakeCompletedException(Long stocktakeId) {
        super(MessageFormat.format("Stocktake with id {0} is already completed and cannot be modified.", stocktakeId),
                new ResourceIdentifier("stocktakeId", stocktakeId));
    }
    
}
