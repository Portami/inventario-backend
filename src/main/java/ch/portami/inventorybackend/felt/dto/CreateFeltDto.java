package ch.portami.inventorybackend.felt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Schema(description = "Request body for creating a new felt color variant.")
public record CreateFeltDto(
    @Schema(description = "Color name for this color variant (e.g. 'Anthracite').", example = "Anthracite")
    @NotBlank String color,

    @Schema(description = "Supplier's own color designation. Can differ from the internal color name.", example = "AN-03")
    @NotBlank String supplierColor,

    @Schema(description = "Thickness of the felt in millimetres. Must be positive.", example = "3.0")
    @NotNull @Positive Double thickness,

    @Schema(description = "Density of the felt in grams per square metre. Must be positive.", example = "300.0")
    @NotNull @Positive Double density,

    @Schema(description = "Purchase price per unit. Must be zero or greater.", example = "12.50")
    @NotNull @DecimalMin("0.00") BigDecimal price,

    @Schema(description = "Supplier's article number that identifies the product line.", example = "ART-001")
    @NotBlank String articleNumber,

    @Schema(description = "ID of an existing supplier. A 404 is returned if the supplier does not exist.")
    @NotNull Long supplierId,

    @Schema(description = "Felt type name (e.g. 'Wool'). A matching FeltType is looked up by name; a new one is created automatically if none exists.", example = "Wool")
    @NotBlank String feltTypeName
) {

}
