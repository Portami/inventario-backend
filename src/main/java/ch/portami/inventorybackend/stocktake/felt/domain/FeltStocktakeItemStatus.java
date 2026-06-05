package ch.portami.inventorybackend.stocktake.felt.domain;

/**
 * Status of a felt stocktake item.
 */
public enum FeltStocktakeItemStatus {
    /**
     * The item has not yet been scanned during the stocktake. The storage where this item is expected is not closed yet
     * but part of the stocktake.
     */
    INITIAL,

    /**
     * The item is expected to be in a storage that is not part of the stocktake.
     */
    OUT_OF_SCOPE,

    /**
     * The item has been scanned in the expected storage.
     */
    OK,

    /**
     * The item is expected to be scanned but was not scanned yet. The storage where this item is expected is already
     * closed.
     */
    MISSING,

    /**
     * The item was found but is located in a different storage than expected.
     */
    WRONG_STORAGE,

    /**
     * The item was previously scanned in the wrong storage, it was marked to be physically moved to the correct
     * storage, and it needs to be scanned again for the correct storage to confirm that it is now in the correct
     * storage.
     */
    RESCAN_REQUIRED,

    /**
     * The item has been scanned more than once, indicating a potential issue with the scanning process. This problem
     * has to be manually resolved by voiding one of the conflicting scans. This problem may hide other problems that
     * will become visible again after the duplicate scan problem is resolved.
     */
    DUPLICATE_SCAN,

    /**
     * The item was scanned but is not part of the current stocktake, e.g. because it was not assigned to any storage at
     * the time the stocktake was started, or because it was created after the stocktake was started.
     */
    NOT_IN_STOCKTAKE,

    /**
     * The scanned barcode does not match any item in the system.
     */
    UNKNOWN
}
