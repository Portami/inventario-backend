package ch.portami.inventorybackend.product.dto.productattributevalue;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Type for setting the value of an existing product attribute when updating a product variant.")
public record ProductAttributeValueChangeDto(
        @Schema(description = "ID of an existing product attribute")
        @NotNull Long attributeId,

        @Schema(description = "Value of the product attribute for this product variant", example = "Red")
        @NotNull String value
) {

}
