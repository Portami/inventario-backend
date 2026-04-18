package ch.portami.inventorybackend.felt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request body for updating an existing felt roll. The felt a roll belongs to cannot be changed — delete and re-create the roll if reassignment is needed.")
public record UpdateFeltRollDto(
    @Schema(description = "New length of the roll in metres. Must be positive.", example = "8.5")
    @NotNull @Positive Double length,

    @Schema(description = "New width of the roll in metres. Must be positive.", example = "1.5")
    @NotNull @Positive Double width,

    @Schema(description = "ID of an existing batch to associate with the roll. Pass null to clear the current batch assignment.", nullable = true)
    Long batchId,

    @Schema(description = "ID of an existing storage location to assign the roll to. Pass null to clear the current storage assignment.", nullable = true)
    Long storageId
) {}
