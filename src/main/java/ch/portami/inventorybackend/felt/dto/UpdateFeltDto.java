package ch.portami.inventorybackend.felt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Schema(description = "Request body for updating an existing felt color variant. All fields are required — supply the current value for any field you do not want to change.")
public record UpdateFeltDto(
    @Schema(description = "New color name for this color variant.", example = "Anthracite")
    @NotBlank String color,

    @Schema(description = "Supplier's own color designation.", example = "AN-03")
    @NotBlank String supplierColor,

    @Schema(description = "Thickness in millimetres. Must be positive.", example = "3.0")
    @NotNull @Positive Double thickness,

    @Schema(description = "Density in grams per square metre. Must be positive.", example = "300.0")
    @NotNull @Positive Double density,

    @Schema(description = "Purchase price per unit. Must be zero or greater.", example = "12.50")
    @NotNull @DecimalMin("0.00") BigDecimal price,

    @Schema(description = "Supplier article number. Changing this (together with supplierId and feltTypeName) may cause the color variant to be re-pointed to a different internal Felt record.", example = "ART-001")
    @NotBlank String articleNumber,

    @Schema(description = "ID of the supplier. A 404 is returned if the supplier does not exist.")
    @NotNull Long supplierId,

    @Schema(description = "Felt type name. A matching FeltType is looked up by name; a new one is created automatically if none exists.", example = "Wool")
    @NotBlank String feltTypeName
) {

}
