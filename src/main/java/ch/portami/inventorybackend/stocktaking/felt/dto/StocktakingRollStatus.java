package ch.portami.inventorybackend.stocktaking.felt.dto;

public enum StocktakingRollStatus {
    INITIAL,
    OK,
    MISSING,
    WRONG_STORAGE,
    DUPLICATE_SCAN,
    ALREADY_REMOVED,
    UNKNOWN
}
