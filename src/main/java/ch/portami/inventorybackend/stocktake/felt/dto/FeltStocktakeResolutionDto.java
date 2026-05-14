package ch.portami.inventorybackend.stocktake.felt.dto;

import jakarta.annotation.Nullable;

public record FeltStocktakeResolutionDto(
        FeltStocktakeResolutionType resolution,
        @Nullable Long newStorageId,
        @Nullable String comment
) {

}

