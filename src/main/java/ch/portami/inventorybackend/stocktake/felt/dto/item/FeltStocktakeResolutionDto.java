package ch.portami.inventorybackend.stocktake.felt.dto.item;

import jakarta.annotation.Nullable;

public record FeltStocktakeResolutionDto(
        FeltStocktakeResolutionType resolution,
        @Nullable Long newStorageId,
        @Nullable String newStorageName,
        @Nullable String comment
) {

}

