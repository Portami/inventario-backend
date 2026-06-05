package ch.portami.inventorybackend.stocktake.felt.exception;

import ch.portami.inventorybackend.core.exceptions.BusinessRuleViolationException;
import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import java.text.MessageFormat;

public class NoFeltStocktakeProblemToResolveException extends BusinessRuleViolationException {

    public NoFeltStocktakeProblemToResolveException(Long stocktakeId, Long itemId) {
        super(MessageFormat.format(
                        "Item with id {0} of stocktake with id {1} does not have a problem that can be resolved.",
                        itemId, stocktakeId),
                new ResourceIdentifier("stocktakeId", stocktakeId),
                new ResourceIdentifier("itemId", itemId));
    }

}
