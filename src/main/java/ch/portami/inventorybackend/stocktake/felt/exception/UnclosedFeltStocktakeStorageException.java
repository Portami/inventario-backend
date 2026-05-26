package ch.portami.inventorybackend.stocktake.felt.exception;

import java.text.MessageFormat;

public class UnclosedFeltStocktakeStorageException extends RuntimeException {

    public UnclosedFeltStocktakeStorageException(Long stocktakeId, Long storageId) {
        super(MessageFormat.format("Cannot complete stocktake with id {0} because storage with id {1} is not closed.",
                stocktakeId, storageId));
    }
    
}
