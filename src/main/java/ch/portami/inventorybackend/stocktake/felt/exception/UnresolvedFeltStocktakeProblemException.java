package ch.portami.inventorybackend.stocktake.felt.exception;

import ch.portami.inventorybackend.core.exceptions.BusinessRuleViolationException;
import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeItemStatus;
import java.text.MessageFormat;

public class UnresolvedFeltStocktakeProblemException extends BusinessRuleViolationException {

    public UnresolvedFeltStocktakeProblemException(Long stocktakeId, Long itemId, FeltStocktakeItemStatus status) {
        super(MessageFormat.format(
                        "Cannot complete stocktake with id {0} because item with id {1} and status {2} still has an unresolved problem.",
                        stocktakeId, itemId, status),
                new ResourceIdentifier("stocktakeId", stocktakeId),
                new ResourceIdentifier("itemId", itemId),
                new ResourceIdentifier("status", status));
    }

}
