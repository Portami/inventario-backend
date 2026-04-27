package ch.portami.inventorybackend.product.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for creating a new category")
public record CreateCategoryDto(
        @Schema(description = "Name of the category")
        @NotNull String name
) {

}
