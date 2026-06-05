package ch.portami.inventorybackend.stocktake.felt.domain;

/**
 * The possible resolution types for problems with stocktake items.
 */
public enum FeltStocktakeResolutionType {
    /**
     * Adjust the storage location of the item to match the storage location where it was found during the stocktake.
     */
    ADJUST_STORAGE,

    /**
     * Physically move the item to the correct storage location. The location of the item in the system is correct and
     * will not change.
     */
    MOVE_PHYSICALLY,

    /**
     * Ignore the fact that the item is missing and do not apply any inventory mutation for this item.
     */
    IGNORE_MISSING,

    /**
     * Remove the item from the system once the stocktake is completed, as it is considered to be no longer in
     * inventory.
     */
    REMOVE_MISSING,

    /**
     * Acknowledge the problem. No decision can be made within the stocktake process.
     */
    ACKNOWLEDGE,
}
