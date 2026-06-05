package ch.portami.inventorybackend.product.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Schema(description = "Request body for updating an existing category")
public record UpdateCategoryDto(
        @Schema(description = "Name of the category")
        String name,

        @Schema(description = "Updated field names. Null = leave fields unchanged; empty list = remove all fields. New names are added; missing names are removed; unchanged names keep their IDs.")
        List<@NotBlank String> fieldNames
) {

}
