package ch.portami.inventorybackend.stocktake.felt.dto.item;

import ch.portami.inventorybackend.stocktake.felt.dto.FeltStocktakeItemType;
import ch.portami.inventorybackend.stocktake.felt.dto.scan.FeltStocktakeScanDto;
import jakarta.annotation.Nullable;
import java.util.List;

public record StocktakeItemDto(
        FeltStocktakeItemType type,
        Long itemId,
        Long expectedStorageId,
        String expectedStorageName,
        StocktakeItemStatus status,
        Boolean needsResolution,
        @Nullable FeltStocktakeResolutionDto resolution,
        List<FeltStocktakeScanDto> scans
) {

}

