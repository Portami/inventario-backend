package ch.portami.inventorybackend.product.dto.productattribute;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Type for creating a new product attribute when creating a new product")
public record CreateProductAttributeDto(
        @Schema(description = "Name of the product attribute", example = "Color")
        @NotNull String name
) {

}
