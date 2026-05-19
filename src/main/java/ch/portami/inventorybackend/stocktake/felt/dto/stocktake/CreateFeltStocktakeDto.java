package ch.portami.inventorybackend.stocktake.felt.dto.stocktake;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "Request body to create a new felt stocktake.")
public record CreateFeltStocktakeDto(
        @Schema(description = "A human-readable description of the stocktake.")
        @NotNull String description,

        @Schema(description = "Whether scrap items should be included in the stocktake.")
        @NotNull Boolean includeScrap,

        @Schema(description = "The list of IDs of the storages to be included in the stocktake. Omit it to include all storages, making it a full stocktake.", nullable = true)
        List<@NotNull Long> storageIds
) {

}
