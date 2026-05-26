package ch.portami.inventorybackend.felt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request body for creating a new felt roll.")
public record CreateFeltRollDto(
        @Schema(description = "ID of the felt color variant (the 'felt' resource) that this roll belongs to. A 404 is returned if the felt does not exist.")
        @NotNull Long feltId,

        @Schema(description = "Length of the roll in metres. Must be positive.", example = "10.0")
        @NotNull @Positive Double length,

        @Schema(description = "Width of the roll in metres. Must be positive.", example = "1.5")
        @NotNull @Positive Double width,

        @Schema(description = "ID of an existing delivery batch to associate with this roll. Omit or pass null to leave unassigned.", nullable = true)
        Long batchId,

        @Schema(description = "ID of an existing storage location to assign this roll to. Omit or pass null to leave unassigned.", nullable = true)
        Long storageId
) {

}
