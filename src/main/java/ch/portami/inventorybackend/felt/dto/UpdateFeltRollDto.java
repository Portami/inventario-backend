package ch.portami.inventorybackend.felt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

@Schema(description = """
    Partial update body for a felt roll. Every field is optional — omit any field (or send it as null) \
    to leave it unchanged. The felt a roll belongs to cannot be changed — delete and re-create if \
    reassignment is needed.\
    """)
public record UpdateFeltRollDto(
    @Schema(description = "New length of the roll in metres. Must be positive when provided.", example = "8.5")
    @Positive Double length,

    @Schema(description = "New width of the roll in metres. Must be positive when provided.", example = "1.5")
    @Positive Double width,

    @Schema(description = "ID of an existing batch to associate with the roll. Omit or pass null to leave the current batch assignment unchanged.", nullable = true)
    Long batchId,

    @Schema(description = "ID of an existing storage location to assign the roll to. Omit or pass null to leave the current storage assignment unchanged.", nullable = true)
    Long storageId
) {}
