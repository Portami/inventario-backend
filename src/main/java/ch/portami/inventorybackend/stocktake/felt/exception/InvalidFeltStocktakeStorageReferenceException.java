package ch.portami.inventorybackend.stocktake.felt.exception;

import ch.portami.inventorybackend.core.exceptions.InvalidResourceReferenceException;
import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import java.text.MessageFormat;

public class InvalidFeltStocktakeStorageReferenceException extends InvalidResourceReferenceException {

    public InvalidFeltStocktakeStorageReferenceException(Long stocktakeId, Long storageId) {
        super(MessageFormat.format("The referenced storage with id {0} is not part of stocktake with id {1}", storageId,
                        stocktakeId),
                new ResourceIdentifier("stocktakeId", stocktakeId), new ResourceIdentifier("storageId", storageId));
    }

}
