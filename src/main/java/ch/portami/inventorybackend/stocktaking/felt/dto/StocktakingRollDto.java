package ch.portami.inventorybackend.stocktaking.felt.dto;

import jakarta.annotation.Nullable;
import java.util.List;

public record StocktakingRollDto(
        Long rollId,
        Long expectedStorageId,
        StocktakingRollStatus status,
        Boolean needsResolution,
        @Nullable FeltStocktakingResolutionDto resolution,
        List<FeltStocktakingScanDto> scans
) {

}
