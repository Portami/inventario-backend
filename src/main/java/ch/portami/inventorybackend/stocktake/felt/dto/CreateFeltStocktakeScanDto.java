package ch.portami.inventorybackend.stocktake.felt.dto;

import jakarta.validation.constraints.NotNull;

public record CreateFeltStocktakeScanDto(
        @NotNull String barcode,
        @NotNull Long scannedStorageId
) {

}

