package ch.portami.inventorybackend.felt.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateFeltColorVariantDto(
    @NotBlank String color,
    @NotBlank String supplierColor
) {

}
