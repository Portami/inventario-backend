package ch.portami.inventorybackend.felt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "A felt — the unified, flat representation of a felt product.")
public record FeltDto(
    @Schema(description = "The felt ID.")
    Long id,

    @Schema(description = "Color name (e.g. 'Anthracite').")
    String color,

    @Schema(description = "Supplier-side color designation.")
    String supplierColor,

    @Schema(description = "Thickness in millimetres.")
    Double thickness,

    @Schema(description = "Density in grams per square metre.")
    Double density,

    @Schema(description = "Purchase price per unit.")
    BigDecimal price,

    @Schema(description = "Supplier article number.")
    String articleNumber,

    @Schema(description = "ID of the supplier.")
    Long supplierId,

    @Schema(description = "Display name of the supplier.")
    String supplierName,

    @Schema(description = "Felt type ID.")
    Long feltTypeId,

    @Schema(description = "Felt type name (e.g. 'Wool', 'Synthetic').")
    String feltTypeName,

    @Schema(description = "Whether this felt is flagged as low on supply.")
    boolean lowOnSupply,

    @Schema(description = "Whether a reorder is already in process.")
    boolean reordered
) {

}
