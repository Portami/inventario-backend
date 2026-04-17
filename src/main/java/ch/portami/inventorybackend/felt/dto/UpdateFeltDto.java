package ch.portami.inventorybackend.felt.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record UpdateFeltDto(
    @NotBlank String color,
    @NotBlank String supplierColor,
    @NotNull @Positive Double thickness,
    @NotNull @Positive Double density,
    @NotNull @DecimalMin("0.00") BigDecimal price,
    @NotBlank String articleNumber,
    @NotNull Long supplierId,
    @NotBlank String feltTypeName
) {

}
