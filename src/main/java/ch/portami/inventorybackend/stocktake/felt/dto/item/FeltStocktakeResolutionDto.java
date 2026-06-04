package ch.portami.inventorybackend.stocktake.felt.dto.item;

import ch.portami.inventorybackend.stocktake.felt.domain.FeltStocktakeResolutionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;

@Schema(description = "A resolution for a felt stocktake problem of a specific item.")
public record FeltStocktakeResolutionDto(
        @Schema(description =
                "The type of this resolution. Only certain resolutions are valid for certain types of problems. The possible resolution types and when they may be applied is as follows:<br/><br/> "
                        + " - `ADJUST_STORAGE`: Adjust the storage location of the item to match the storage location where it was found during the stocktake. Only set for items with status `WRONG_STORAGE`.<br/><br/> "
                        + " - `MOVE_PHYSICALLY`: Physically move the item to the correct storage location. The location of the item in the system is correct and will not change. Only set for items with status `WRONG_STORAGE` or `RESCAN_REQUIRED`.<br/><br/> "
                        + " - `IGNORE_MISSING`: Ignore the fact that the item is missing and do not apply any inventory mutation for this item. Only set for items with status `MISSING`.<br/><br/> "
                        + " - `REMOVE_MISSING`: Remove the item from the system once the stocktake is completed, as it is considered to be no longer in inventory. Only set for items with status `MISSING`.<br/><br/> "
                        + " - `ACKNOWLEDGE`: Acknowledge the problem. No decision can be made within the stocktake process. Only applicable for items with status `NOT_IN_STOCKTAKE` or `UNKNOWN`.<br/>")
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

