package ch.portami.inventorybackend.product.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A field definition belonging to a product category")
public record CategoryFieldDto(
        @Schema(description = "ID of the field")
        Long id,

        @Schema(description = "Name of the field")
        String name
) {

}
