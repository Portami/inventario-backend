package ch.portami.inventorybackend.stocktake.felt.dto.scan;

import ch.portami.inventorybackend.stocktake.felt.dto.FeltStocktakeItemType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "A felt stocktake scan, containing all relevant information about a single scan performed during the stocktake process.")
public record FeltStocktakeScanDto(
        @Schema(description = "The unique identifier of the scan.")
        Long scanId,

        @Schema(description = "The type of the scanned item, which can be either `ROLL` for a roll, a `SCRAP` for a scrap piece, or `UNKNOWN` for a scan of an unknown barcode.")
        FeltStocktakeItemType type,

        @Schema(description = "The unique identifier of the scanned item. This is not the same as the roll or scrap ID.")
        Long itemId,

        @Schema(description = "The barcode that was scanned (or manually entered).")
        String barcode,

        @Schema(description = "The ID of the storage location where the item was scanned.")
        Long scannedStorageId,

        @Schema(description = "Indicates whether the scan has been voided. A voided scan is a scan that has been marked as invalid, e.g. because it was performed by mistake or because it is a duplicate of another scan. Voided scans are not considered when determining the status of an item during the stocktake process, but they are kept to better understand the history of the item during the stocktake process.")
        Boolean isVoided,

        @Schema(description = "Indicates whether the scan has been corrected. A corrected scan is a scan that has been marked as corrected or to be corrected. It is only relevant for scans of items with status `WRONG_STORAGE` or `RESCAN_REQUIRED` that have been marked to be physically moved to the correct storage, and it indicates whether the physical move has already happened. If a scan is marked as corrected, it means that the item has already been physically moved to the correct storage and rescanned there, or the stocktaking is completed and the item is expected to be in the correct storage now.")
        Boolean isCorrected,

        @Schema(description = "The timestamp when the scan was performed.")
        Instant scannedAt
) {

}

