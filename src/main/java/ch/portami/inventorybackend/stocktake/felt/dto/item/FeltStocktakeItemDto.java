package ch.portami.inventorybackend.stocktake.felt.dto.item;

import ch.portami.inventorybackend.stocktake.felt.dto.FeltStocktakeItemType;
import ch.portami.inventorybackend.stocktake.felt.dto.scan.FeltStocktakeScanDto;
import jakarta.annotation.Nullable;
import java.util.List;

public record FeltStocktakeItemDto(
        FeltStocktakeItemType type,
        Long itemId,
        @Nullable FeltStocktakeRollOrScrapDto rollOrScrapDto,
        @Nullable String barcode,
        Long expectedStorageId,
        String expectedStorageName,
        FeltStocktakeItemStatus status,
        Boolean needsResolution,
        @Nullable FeltStocktakeResolutionDto resolution,
        List<FeltStocktakeScanDto> scans
) {

}

