package ch.portami.inventorybackend.felt.supply.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Supply status of a felt roll, including enough felt context for UI display.")
public record SupplyDto(
        @Schema(description = "ID of the felt color variant this roll belongs to.")
        Long feltColorVariantId,

        @Schema(description = "Whether this roll is low on supply and needs reordering.")
        boolean lowOnSupply,

        @Schema(description = "Whether a reorder for this roll is already in process.")
        boolean reordered,

        @Schema(description = "Color name of the felt color variant.")
        String color,

        @Schema(description = "Supplier article number.")
        String articleNumber,

        @Schema(description = "Display name of the supplier.")
        String supplierName,

        @Schema(description = "Felt type name (e.g. 'Wool', 'Synthetic').")
        String feltTypeName
) {

}
