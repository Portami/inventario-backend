package ch.portami.inventorybackend.product.dto.productattributevalue;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A product attribute and its value for a specific product variant")
public record ProductAttributeValueDto(
        @Schema(description = "ID of the product attribute this value belongs to")
        Long attributeId,

        @Schema(description = "Name of the product attribute this value belongs to", example = "Color")
        String name,

        @Schema(description = "Value of the product attribute", example = "Red")
        String value
) {

}
