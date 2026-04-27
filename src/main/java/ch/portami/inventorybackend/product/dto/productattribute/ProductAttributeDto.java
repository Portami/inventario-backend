package ch.portami.inventorybackend.product.dto.productattribute;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A product attribute assigned to a product")
public record ProductAttributeDto(
        @Schema(description = "ID of the product attribute")
        long id,

        @Schema(description = "Name of the product attribute", example = "Color")
        String name
) {

}
