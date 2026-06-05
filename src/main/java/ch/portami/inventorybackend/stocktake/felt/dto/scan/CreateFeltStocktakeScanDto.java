package ch.portami.inventorybackend.stocktake.felt.dto.scan;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for creating a new felt stocktake scan. It contains the barcode of the item being scanned and the ID of the storage location where the item is being scanned.")
public record CreateFeltStocktakeScanDto(
        @Schema(description = "The barcode of the item being scanned.")
        @NotNull String barcode,

        @Schema(description = "The ID of the storage location where the item is being scanned.")
        @NotNull Long scannedStorageId
) {

}

