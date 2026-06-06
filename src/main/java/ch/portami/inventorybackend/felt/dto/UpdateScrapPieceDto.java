package ch.portami.inventorybackend.felt.dto;

import ch.portami.inventorybackend.felt.validation.MinScrapSide;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
        Partial update body for a scrap piece. Every field is optional — omit any field (or send it as null) \
        to leave it unchanged. The felt a scrap piece belongs to cannot be changed — delete and re-create if \
        reassignment is needed.\
        """)
public record UpdateScrapPieceDto(
        @Schema(description = "New length in centimetres. When provided, must be at least the configured minimum scrap side.", example = "55.0")
        @MinScrapSide Double length,

        @Schema(description = "New width in centimetres. When provided, must be at least the configured minimum scrap side.", example = "48.0")
        @MinScrapSide Double width,

        @Schema(description = "ID of an existing batch. Omit or pass null to leave unchanged.", nullable = true)
        Long batchId,

        @Schema(description = "ID of an existing storage location. Omit or pass null to leave unchanged.", nullable = true)
        Long storageId
) {

}
