package ch.portami.inventorybackend.product.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "A product category")
public record CategoryDto(
        @Schema(description = "ID of the category")
        Long id,

        @Schema(description = "Name of the category")
        String name,

        @Schema(description = "Field definitions belonging to this category")
        List<CategoryFieldDto> fields
) {

}
