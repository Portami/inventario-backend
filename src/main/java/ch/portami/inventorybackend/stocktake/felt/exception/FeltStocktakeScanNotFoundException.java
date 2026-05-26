package ch.portami.inventorybackend.stocktake.felt.exception;

import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;
import java.text.MessageFormat;

public class FeltStocktakeScanNotFoundException extends ResourceNotFoundException {

    public FeltStocktakeScanNotFoundException(Long stocktakeId, Long scanId) {
        super(MessageFormat.format("Stocktake with id {0} does not contain a scan with id {1}", stocktakeId, scanId),
                new ResourceIdentifier("stocktakeId", stocktakeId),
                new ResourceIdentifier("scanId", scanId));
    }
    
}
