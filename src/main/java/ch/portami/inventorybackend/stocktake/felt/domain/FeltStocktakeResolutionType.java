package ch.portami.inventorybackend.stocktake.felt.domain;

public enum FeltStocktakeResolutionType {
    ADJUST_STORAGE,
    MOVE_PHYSICALLY,
    IGNORE_MISSING,
    REMOVE_MISSING,
    ACKNOWLEDGE,
}
