package ch.portami.inventorybackend.stocktake.felt.dto;

public enum StocktakeRollStatus {
    INITIAL,
    OK,
    MISSING,
    WRONG_STORAGE,
    DUPLICATE_SCAN,
    ALREADY_REMOVED,
    UNKNOWN
}

