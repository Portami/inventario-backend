package ch.portami.inventorybackend.felt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * One leftover scrap piece to keep from the material cut off a roll during an "Abschneiden"
 * operation. Unlike {@link CreateScrapPieceDto}, no minimum size is enforced here: a piece that is
 * too small is silently dropped by the cut operation rather than rejected.
 */
@Schema(description = "A leftover scrap piece to keep from a roll cut. Pieces with any side below the configured "
        + "minimum are silently dropped, not saved.")
public record CutScrapDto(
        @Schema(description = "Length of the scrap piece in centimetres.", example = "60.0")
        @NotNull @Positive Double length,

        @Schema(description = "Width of the scrap piece in centimetres.", example = "50.0")
        @NotNull @Positive Double width,

        @Schema(description = "ID of a batch to assign. Omit or pass null to inherit the source roll's batch.", nullable = true)
        Long batchId,

        @Schema(description = "ID of a storage location to assign. Omit or pass null to inherit the source roll's storage.", nullable = true)
        Long storageId
) {

}
