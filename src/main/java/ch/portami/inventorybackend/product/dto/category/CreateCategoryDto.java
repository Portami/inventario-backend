package ch.portami.inventorybackend.product.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "Request body for creating a new category")
public record CreateCategoryDto(
        @Schema(description = "Name of the category")
        @NotNull String name,

        @Schema(description = "Initial field names for this category (optional)")
        List<@NotBlank String> fieldNames
) {

}
