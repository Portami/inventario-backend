package ch.portami.inventorybackend.stocktake.felt.dto.stocktake;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import java.time.Instant;
import java.util.List;

@Schema(description = "A felt stocktake with included storage lists and completion details.")
public record FeltStocktakeDto(
        @Schema(description = "The unique identifier of the stocktake.")
        Long id,

        @Schema(description = "A human-readable description of the stocktake.")
        String description,

        @Schema(description = "The timestamp when the stocktake was created.")
        Instant createdAt,

        @Schema(description = "The list of storage lists included in this stocktake.")
        List<FeltStocktakeListInfoDto> storageLists,

        @Schema(description = "Whether the stocktake is completed.")
        Boolean isCompleted,

        @Schema(description = "The timestamp when the stocktake was completed.", nullable = true)
        @Nullable Instant completedAt
) {

}
