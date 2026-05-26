package ch.portami.inventorybackend.stocktake.felt.exception;

import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;
import java.text.MessageFormat;

public class FeltStocktakeNotFoundException extends ResourceNotFoundException {

    public FeltStocktakeNotFoundException(Long id) {
        super(MessageFormat.format("Stocktake with id {0} was not found.", id),
                new ResourceIdentifier("stocktakeId", id));
    }
    
}
