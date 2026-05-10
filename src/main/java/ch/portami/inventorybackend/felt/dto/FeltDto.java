package ch.portami.inventorybackend.felt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "A felt color variant — the unified, flattened representation of the full product hierarchy (FeltType → Felt → FeltVariant → FeltColorVariant).")
public record FeltDto(
    @Schema(description = "The felt color variant ID. This is the primary API identifier used in all felt endpoints and in roll creation.")
    Long id,

    @Schema(description = "Color name of this specific color variant (e.g. 'Anthracite').")
    String color,

    @Schema(description = "Supplier-side color designation (e.g. supplier's own color code or name). May differ from the internal color name.")
    String supplierColor,

    @Schema(description = "Thickness of the felt in millimetres.")
    Double thickness,

    @Schema(description = "Density of the felt in grams per square metre.")
    Double density,

    @Schema(description = "Purchase price per unit (currency depends on tenant configuration).")
    BigDecimal price,

    @Schema(description = "Internal FeltVariant ID grouping all color variants that share the same thickness, density, and price. Not an API resource — provided for reference only.")
    Long feltVariantId,

    @Schema(description = "Supplier article number that uniquely identifies the felt product line within a supplier.")
    String articleNumber,

    @Schema(description = "ID of the supplier.")
    Long supplierId,

    @Schema(description = "Display name of the supplier.")
    String supplierName,

    @Schema(description = "Internal Felt entity ID grouping all variants that share the same article number and supplier. Not an API resource — provided for reference only.")
    Long feltId,

    @Schema(description = "Internal FeltType ID.")
    Long feltTypeId,

    @Schema(description = "Felt type name (e.g. 'Wool', 'Synthetic').")
    String feltTypeName,

    @Schema(description = "Whether this roll is flagged as low on supply and needs reordering.")
    boolean lowOnSupply,

    @Schema(description = "Whether a reorder for this roll is already in process.")
    boolean reordered
) {

}
