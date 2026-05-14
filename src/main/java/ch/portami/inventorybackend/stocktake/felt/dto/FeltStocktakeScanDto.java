package ch.portami.inventorybackend.stocktake.felt.dto;

import jakarta.annotation.Nullable;
import java.time.Instant;

public record FeltStocktakeScanDto(
        Long scanId,
        @Nullable FeltStocktakeItemType type,
        @Nullable Long itemId,
        String barcode,
        Long scannedStorageId,
        Boolean isVoided,
        Instant scannedAt
) {

}

