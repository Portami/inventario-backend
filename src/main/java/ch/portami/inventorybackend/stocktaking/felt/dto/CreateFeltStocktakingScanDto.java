package ch.portami.inventorybackend.stocktaking.felt.dto;

import jakarta.validation.constraints.NotNull;

public record CreateFeltStocktakingScanDto(
        @NotNull String barcode,
        @NotNull Long scannedStorageId
) {

}
