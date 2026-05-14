package ch.portami.inventorybackend.stocktake.felt.dto.stocktake;

import jakarta.annotation.Nullable;
import java.time.Instant;
import java.util.List;

public record FeltStocktakeDto(
        Long id,
        String description,
        Instant createdAt,
        List<FeltStocktakeListInfoDto> storageLists,
        Boolean isCompleted,
        @Nullable Instant completedAt
) {


}

