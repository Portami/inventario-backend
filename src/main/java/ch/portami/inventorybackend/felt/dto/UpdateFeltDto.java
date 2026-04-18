package ch.portami.inventorybackend.felt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Schema(description = """
    Partial update body for a felt color variant. Every field is optional — omit any field \
    (or send it as null) to leave it unchanged. Only the fields you provide are applied.\
    """)
public record UpdateFeltDto(
    @Schema(description = "New color name for this color variant.", example = "Anthracite")
    String color,

    @Schema(description = "Supplier's own color designation.", example = "AN-03")
    String supplierColor,

    @Schema(description = "Thickness in millimetres. Must be positive when provided.", example = "3.0")
    @Positive Double thickness,

    @Schema(description = "Density in grams per square metre. Must be positive when provided.", example = "300.0")
    @Positive Double density,

    @Schema(description = "Purchase price per unit. Must be zero or greater when provided.", example = "12.50")
    @DecimalMin("0.00") BigDecimal price,

    @Schema(description = "Supplier article number.", example = "ART-001")
    String articleNumber,

    @Schema(description = "ID of the supplier. A 404 is returned if the supplier does not exist.")
    Long supplierId,

    @Schema(description = "ID of the felt type. A 404 is returned if the felt type does not exist.")
    Long feltTypeId
) {

}
