package ch.portami.inventorybackend.product.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A product category")
public record CategoryDto(
        @Schema(description = "ID of the category")
        long id,

        @Schema(description = "Name of the category")
        String name
) {

}
