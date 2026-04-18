package ch.portami.inventorybackend.product.dto.productattributevalue;

import jakarta.validation.constraints.NotNull;

public record CreateProductAttributeValueDto(
        @NotNull Long attributeId,
        String value
) {

}
