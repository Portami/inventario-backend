package ch.portami.inventorybackend.stocktaking.felt.dto;

import jakarta.annotation.Nullable;
import java.time.Instant;

public record FeltStocktakingScanDto(
        Long scanId,
        @Nullable Long rollId,
        String barcode,
        Long scannedStorageId,
        Boolean isVoided,
        Instant scannedAt
) {

}
