package ch.portami.inventorybackend.stocktake.felt.domain;

public enum FeltStocktakeItemStatus {
    INITIAL,
    OK,
    MISSING,
    WRONG_STORAGE,
    RESCAN_REQUIRED,
    DUPLICATE_SCAN,
    NOT_IN_STOCKTAKE,
    UNKNOWN
}
