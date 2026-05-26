package ch.portami.inventorybackend.stocktake.felt.exception;

import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;
import java.text.MessageFormat;

public class FeltStocktakeItemNotFoundException extends ResourceNotFoundException {

    public FeltStocktakeItemNotFoundException(Long stocktakeId, Long itemId) {
        super(MessageFormat.format("Stocktake with id {0} does not contain an item with id {1}", stocktakeId, itemId),
                new ResourceIdentifier("stocktakeId", stocktakeId),
                new ResourceIdentifier("itemId", itemId));
    }

}
