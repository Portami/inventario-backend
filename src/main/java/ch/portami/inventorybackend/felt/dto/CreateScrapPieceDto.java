package ch.portami.inventorybackend.felt.dto;

import ch.portami.inventorybackend.felt.validation.MinScrapSide;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for creating a new scrap piece.")
public record CreateScrapPieceDto(
        @Schema(description = "ID of the felt color variant this scrap piece belongs to. A 404 is returned if the felt does not exist.")
        @NotNull Long feltId,

        @Schema(description = "Length of the scrap piece in centimetres. Must be at least the configured minimum scrap side (default 44.0).", example = "60.0")
        @NotNull @MinScrapSide Double length,

        @Schema(description = "Width of the scrap piece in centimetres. Must be at least the configured minimum scrap side (default 44.0).", example = "50.0")
        @NotNull @MinScrapSide Double width,

        @Schema(description = "ID of an existing delivery batch to associate with this scrap piece. Omit or pass null to leave unassigned.", nullable = true)
        Long batchId,

        @Schema(description = "ID of an existing storage location to assign this scrap piece to. Omit or pass null to leave unassigned.", nullable = true)
        Long storageId
) {

}
