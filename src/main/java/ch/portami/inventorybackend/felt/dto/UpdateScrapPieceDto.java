package ch.portami.inventorybackend.felt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;

@Schema(description = """
    Partial update body for a scrap piece. Every field is optional — omit any field (or send it as null) \
    to leave it unchanged. The felt a scrap piece belongs to cannot be changed — delete and re-create if \
    reassignment is needed.\
    """)
public record UpdateScrapPieceDto(
    @Schema(description = "New length in centimetres. Must be at least 44.0 when provided.", example = "55.0")
    @DecimalMin("44.0") Double length,

    @Schema(description = "New width in centimetres. Must be at least 44.0 when provided.", example = "48.0")
    @DecimalMin("44.0") Double width,

    @Schema(description = "ID of an existing batch. Omit or pass null to leave unchanged.", nullable = true)
    Long batchId,

    @Schema(description = "ID of an existing storage location. Omit or pass null to leave unchanged.", nullable = true)
    Long storageId
) {}
