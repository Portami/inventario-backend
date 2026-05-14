package ch.portami.inventorybackend.stocktaking.felt.dto;

import jakarta.validation.constraints.NotNull;

public record ResolveFeltStocktakingProblemDto(
        @NotNull FeltStocktakingResolutionType resolution,
        Long targetStorageId,
        String comment
) {

}
