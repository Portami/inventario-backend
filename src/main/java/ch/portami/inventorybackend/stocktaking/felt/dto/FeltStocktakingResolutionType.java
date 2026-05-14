package ch.portami.inventorybackend.stocktaking.felt.dto;

public enum FeltStocktakingResolutionType {
    ADJUST_STORAGE,
    MOVE_PHYSICALLY,
    IGNORE_MISSING,
    REMOVE_MISSING,
    RESTORE,
    IGNORE_UNKNOWN
}
