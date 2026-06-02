package ch.portami.inventorybackend.stocktake.felt.dto.item;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;

@Schema(description = "A resolution for a felt stocktake problem of a specific item.")
public record FeltStocktakeResolutionDto(
        // No schema definition for this field as it would overwrite the more detailed descriptions on the enum type.
        FeltStocktakeResolutionType resolution,

        @Schema(description = "Indicates whether an actual inventory mutation has been applied on completion of the stocktaking. If the stocktake is not completed yet, it will indicate if one will happen if the stocktake would be completed now.")
        Boolean mutationApplied,

        @Schema(description = "The ID of the new storage location if the resolution resolves a `WRONG_STORAGE` problem. It is null for other types of resolutions.", nullable = true)
        @Nullable Long newStorageId,

        @Schema(description = "The name of the new storage location if the resolution resolves a `WRONG_STORAGE` problem. It is null for other types of resolutions.", nullable = true)
        @Nullable String newStorageName,

        @Schema(description = "An optional comment provided by the user when applying the resolution. It can provide additional context or information about the reason for the chosen resolution.", nullable = true)
        @Nullable String comment
) {

}

