package ch.portami.inventorybackend.product.dto.product;

import ch.portami.inventorybackend.product.dto.productattribute.CreateProductAttributeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "Request body for creating a new product")
public record CreateProductDto(
        @Schema(description = "Product name")
        @NotNull String name,

        @Schema(description = "ID of an existing category")
        @NotNull Long categoryId,

        @Schema(description = "List of attributes of this product")
        List<@NotNull @Valid CreateProductAttributeDto> attributes
) {

}
