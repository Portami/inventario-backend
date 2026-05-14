package ch.portami.inventorybackend.stocktake.felt.dto;

import jakarta.validation.constraints.NotNull;

public record ResolveFeltStocktakeProblemDto(
        @NotNull FeltStocktakeResolutionType resolution,
        Long targetStorageId,
        String comment
) {

}

