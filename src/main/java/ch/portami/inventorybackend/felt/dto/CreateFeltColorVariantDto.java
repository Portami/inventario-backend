package ch.portami.inventorybackend.felt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateFeltColorVariantDto(
    @NotNull Long feltVariantId,
    @NotBlank String color,
    @NotBlank String supplierColor
) {

}
