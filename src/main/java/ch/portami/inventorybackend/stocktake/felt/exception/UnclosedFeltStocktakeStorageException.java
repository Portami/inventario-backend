package ch.portami.inventorybackend.stocktake.felt.exception;

import ch.portami.inventorybackend.core.exceptions.BusinessRuleViolationException;
import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import java.text.MessageFormat;

public class UnclosedFeltStocktakeStorageException extends BusinessRuleViolationException {

    public UnclosedFeltStocktakeStorageException(Long stocktakeId, Long storageId) {
        super(MessageFormat.format("Cannot complete stocktake with id {0} because storage with id {1} is not closed.",
                        stocktakeId, storageId),
                new ResourceIdentifier("stocktakeId", stocktakeId),
                new ResourceIdentifier("storageId", storageId));
    }

}
