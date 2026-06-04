package ch.portami.inventorybackend.stocktake.felt.domain;

public enum FeltStocktakeItemStatus {
    INITIAL,
    OUT_OF_SCOPE,
    OK,
    MISSING,
    WRONG_STORAGE,
    RESCAN_REQUIRED,
    DUPLICATE_SCAN,
    NOT_IN_STOCKTAKE,
    UNKNOWN
}
