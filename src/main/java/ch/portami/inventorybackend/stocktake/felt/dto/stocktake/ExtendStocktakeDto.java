package ch.portami.inventorybackend.stocktake.felt.dto.stocktake;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "Request body to extend a felt stocktake with additional storages.")
public record ExtendStocktakeDto(
        @Schema(description = "The list of the IDs of the storages to be added to the stocktake. Omit it to include all storages, making it a full stocktake.", nullable = true)
        List<@NotNull Long> storageIds
) {

}
