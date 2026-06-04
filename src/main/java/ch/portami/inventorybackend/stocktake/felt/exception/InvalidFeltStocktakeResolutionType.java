package ch.portami.inventorybackend.stocktake.felt.exception;

import ch.portami.inventorybackend.core.exceptions.BusinessRuleViolationException;
import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeItemApiStatus;
import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeResolutionType;
import java.text.MessageFormat;

public class InvalidFeltStocktakeResolutionType extends BusinessRuleViolationException {

    public InvalidFeltStocktakeResolutionType(Long stocktakeId, Long itemId, FeltStocktakeItemApiStatus itemStatus,
            FeltStocktakeResolutionType resolutionType) {
        super(MessageFormat.format("Resolution type {0} is not valid for item {1} with status {2}.", resolutionType,
                        itemId, itemStatus),
                new ResourceIdentifier("stocktakeId", stocktakeId),
                new ResourceIdentifier("itemId", itemId),
                new ResourceIdentifier("itemStatus", itemStatus),
                new ResourceIdentifier("resolutionType", resolutionType));
    }

}
