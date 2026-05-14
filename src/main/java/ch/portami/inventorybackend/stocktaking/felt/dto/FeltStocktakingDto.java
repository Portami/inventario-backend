package ch.portami.inventorybackend.stocktaking.felt.dto;

import jakarta.annotation.Nullable;
import java.time.Instant;
import java.util.List;

public record FeltStocktakingDto(
        Long id,
        String description,
        Instant createdAt,
        List<FeltStocktakingListInfoDto> storageLists,
        Boolean isCompleted,
        @Nullable Instant completedAt
) {


}
