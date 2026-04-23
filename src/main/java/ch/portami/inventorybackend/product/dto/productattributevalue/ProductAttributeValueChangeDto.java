package ch.portami.inventorybackend.product.dto.productattributevalue;

import jakarta.validation.constraints.NotNull;

public record ProductAttributeValueChangeDto(
        @NotNull Long attributeId,
        @NotNull String value
) {

}
