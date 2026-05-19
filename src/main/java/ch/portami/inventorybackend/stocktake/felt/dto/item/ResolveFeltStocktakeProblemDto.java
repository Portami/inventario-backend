package ch.portami.inventorybackend.stocktake.felt.dto.item;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for resolving a problem encountered during a felt stocktake. It contains the resolution type and an optional comment providing additional context for the resolution decision.")
public record ResolveFeltStocktakeProblemDto(
        // No schema definition for this field as it would overwrite the more detailed descriptions on the enum type.
        @NotNull FeltStocktakeResolutionType resolution,

        @Schema(description = "An optional comment provided by the user when applying the resolution. It can provide additional context or information about the reason for the chosen resolution.", nullable = true)
        String comment
) {

}

