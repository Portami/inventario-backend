package ch.portami.inventorybackend.restocking.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Partial update for the supply flags of a felt roll. Omit a field to leave it unchanged.")
public record UpdateSupplyDto(
    @Schema(description = "Whether this roll is low on supply and needs reordering.", nullable = true)
    Boolean lowOnSupply,

    @Schema(description = "Whether a reorder for this roll is already in process.", nullable = true)
    Boolean reordered
) {}
