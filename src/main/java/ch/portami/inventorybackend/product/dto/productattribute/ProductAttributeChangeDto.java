package ch.portami.inventorybackend.product.dto.productattribute;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Type for creating or updating a product attribute of an already existing product. To update an existing product attribute, provide its ID. To create a new product attribute, omit the ID or set it to null.")
public record ProductAttributeChangeDto(
        @Schema(description = "ID of the product attribute. Omit or set to null to create a new product attribute, provide an existing ID to update an existing product attribute.")
        Long id,
        
        @Schema(description = "Name of the product attribute", example = "Color")
        @NotNull String name
) {

}
