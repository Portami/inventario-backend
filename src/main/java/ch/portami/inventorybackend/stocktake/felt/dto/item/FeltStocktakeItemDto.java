package ch.portami.inventorybackend.stocktake.felt.dto.item;

import ch.portami.inventorybackend.stocktake.felt.dto.FeltStocktakeItemType;
import ch.portami.inventorybackend.stocktake.felt.dto.scan.FeltStocktakeScanDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import java.util.List;

@Schema(description = "An item of a felt stocktake, including its expected details, status, any scans performed, and a potential resolution if there were problems identified during the stocktake process.")
public record FeltStocktakeItemDto(
        @Schema(description = "The type of the item, which can be either `ROLL` for a roll, a `SCRAP` for a scrap piece, or `UNKNOWN` for an unknown-barcode item, i.e. a pseudo item representing an unassigned barcode that was scanned during the stocktake process.")
        FeltStocktakeItemType type,

        @Schema(description = "The unique identifier of the item. It is not the same as the roll or scrap ID and unique even across stocktakes.")
        Long itemId,

        @Schema(description = "The roll or scrap details of the item. It is null for unknown-barcode items.", nullable = true)
        @Nullable FeltStocktakeRollOrScrapDto rollOrScrap,

        @Schema(description = "The barcode of the item. It is only set for unknown-barcode items, and is null for all other items.", nullable = true)
        @Nullable String barcode,

        @Schema(description = "The ID of the expected storage location. It is null for items not assigned to any storage (item with status `NOT_IN_STOCKTAKE`), and for unknown-barcode items (status `UNKNOWN`).", nullable = true)
        @Nullable Long expectedStorageId,

        @Schema(description = "The name of the expected storage location. It is null for items not assigned to any storage (item with status `NOT_IN_STOCKTAKE`), and for unknown-barcode items (status `UNKNOWN`).", nullable = true)
        @Nullable String expectedStorageName,

        @Schema(description =
                "The status the item, indicating whether it has been scanned and if there are any problems with it. Items with resolved problems keep their status, but have `needsResolution` set to false. The possible statuses are:<br/><br/> "
                        + " - `INITIAL`: The item has not yet been scanned during the stocktake. The storage where this item is expected is not closed yet.<br/><br/> "
                        + " - `OK`: The item has been scanned for the expected storage.<br/><br/> "
                        + " - `MISSING`: The item was expected but was not found during the scan. The storage where this item is expected is already closed. Possible resolutions are `REMOVE_MISSING` and `IGNORE_MISSING`<br/><br/> "
                        + " - `WRONG_STORAGE`: The item was found but is located in a different storage than expected. Possible resolutions are `ADJUST_STORAGE` and `MOVE_PHYSICALLY`.<br/><br/> "
                        + " - `RESCAN_REQUIRED`: The item was previously scanned in the wrong storage, it was marked to be physically moved to the correct storage, and it needs to be scanned again for the correct storage to confirm that it is now in the correct storage.<br/><br/> "
                        + " - `DUPLICATE_SCAN`: The item has been scanned more than once, indicating a potential issue with the scanning process. This problem has to be manually resolved by voiding one of the conflicting scans. This problem may hide other problems (e.g. `WRONG_STORAGE`) that will become visible again after the duplicate scan problem is resolved.<br/><br/> "
                        + " - `NOT_IN_STOCKTAKE`: The item was scanned but is not part of the current stocktake, e.g. because it has not was not assigned to any storage at the time the stocktake was started, or because it was created after the stocktake was started. The problem has to be acknowledged (`ACKNOWLEDGE`).<br/><br/> "
                        + " - `UNKNOWN`: The scanned barcode does not match any item in the system. This problem has to be resolved manually, e.g. by creating a new item for the scanned barcode, and must be resolved by acknowledging the problem (`ACKNOWLEDGE`).<br/>")
        FeltStocktakeItemApiStatus status,

        @Schema(description = "Indicates whether the item has any unresolved problems.")
        Boolean needsResolution,

        @Schema(description = "The resolution for the problem of the item if it has one. It is null if there are no problems or if the problem has not yet been resolved.", nullable = true)
        @Nullable FeltStocktakeResolutionDto resolution,

        @Schema(description = "The list of scans that have been performed for this item during the stocktake process. This includes all scans, even those that have been voided or corrected, as they may still be relevant for understanding the history of the item during the stocktake process. For completed stocktakes, voided scans are not present anymore.")
        List<FeltStocktakeScanDto> scans
) {

}

