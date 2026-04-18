package ch.portami.inventorybackend.product.dto.productattribute;

import jakarta.validation.constraints.NotNull;

public record ProductAttributeChangeDto(
        Long id,
        @NotNull String name
) {

}
