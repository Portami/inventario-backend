package ch.portami.inventorybackend.stocktake.felt.dto.stocktake;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A storage list included in a felt stocktake.")
public record FeltStocktakeListInfoDto(
        @Schema(description = "The ID of the storage this list belongs to.")
        Long storageId,

        @Schema(description = "The name of the storage this list belongs to.")
        String storageName,

        @Schema(description = "Whether the stocktake list is closed.")
        Boolean isClosed
) {

}
