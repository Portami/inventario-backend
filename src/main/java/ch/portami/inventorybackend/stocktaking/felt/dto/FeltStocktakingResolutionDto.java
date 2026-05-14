package ch.portami.inventorybackend.stocktaking.felt.dto;

import jakarta.annotation.Nullable;

public record FeltStocktakingResolutionDto(
        FeltStocktakingResolutionType resolution,
        @Nullable Long newStorageId,
        @Nullable String comment
) {

}
