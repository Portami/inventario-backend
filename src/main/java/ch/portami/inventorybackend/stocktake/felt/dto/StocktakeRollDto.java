package ch.portami.inventorybackend.stocktake.felt.dto;

import jakarta.annotation.Nullable;
import java.util.List;

public record StocktakeRollDto(
        Long rollId,
        Long expectedStorageId,
        StocktakeRollStatus status,
        Boolean needsResolution,
        @Nullable FeltStocktakeResolutionDto resolution,
        List<FeltStocktakeScanDto> scans
) {

}

