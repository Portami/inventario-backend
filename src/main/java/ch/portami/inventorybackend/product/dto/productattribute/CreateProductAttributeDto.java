package ch.portami.inventorybackend.product.dto.productattribute;

import jakarta.validation.constraints.NotNull;

public record CreateProductAttributeDto(
        @NotNull String name
) {

}
