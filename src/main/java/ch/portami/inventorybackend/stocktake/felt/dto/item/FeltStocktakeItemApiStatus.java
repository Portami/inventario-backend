package ch.portami.inventorybackend.stocktake.felt.dto.item;

public enum FeltStocktakeItemApiStatus {
    INITIAL,
    OK,
    MISSING,
    WRONG_STORAGE,
    RESCAN_REQUIRED,
    DUPLICATE_SCAN,
    NOT_IN_STOCKTAKE,
    UNKNOWN
}

