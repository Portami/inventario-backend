package ch.portami.inventorybackend.stocktake.felt.dto;

public enum StocktakeItemStatus {
    INITIAL,
    OK,
    MISSING,
    WRONG_STORAGE,
    DUPLICATE_SCAN,
    ALREADY_REMOVED,
    UNKNOWN
}

