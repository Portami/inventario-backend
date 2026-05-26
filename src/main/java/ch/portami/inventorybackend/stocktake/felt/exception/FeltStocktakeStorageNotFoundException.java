package ch.portami.inventorybackend.stocktake.felt.exception;

import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;
import java.text.MessageFormat;

public class FeltStocktakeStorageNotFoundException extends ResourceNotFoundException {

    public FeltStocktakeStorageNotFoundException(Long stocktakeId, Long storageId) {
        super(MessageFormat.format("Storage with id {0} is not part of stocktake with id {1}", storageId, stocktakeId),
                new ResourceIdentifier("stocktakeId", stocktakeId), new ResourceIdentifier("storageId", storageId));
    }
    
}
