package ch.portami.inventorybackend.product.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body for updating an existing category")
public record UpdateCategoryDto(
        @Schema(description = "Name of the category", example = "Electronics")
        String name
) {

}
