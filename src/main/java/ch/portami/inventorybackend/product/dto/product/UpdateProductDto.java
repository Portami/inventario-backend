package ch.portami.inventorybackend.product.dto.product;

import ch.portami.inventorybackend.product.dto.productattribute.ProductAttributeChangeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = """
        Partial update body for a product. Every field is optional — omit any field \
        (or send it as null) to leave it unchanged. Only the fields you provide are applied. \
        If the attributes field is provided, it will replace the entire list of attributes of the product. \
        To keep existing attributes, they must be included in the list with their ID. To remove an attribute, simply omit it from the list. To add a new attribute, include it in the list without an ID.\
        """)
public record UpdateProductDto(
        @Schema(description = "New product name")
        String name,

        @Schema(description = "ID of a different existing category")
        Long categoryId,

        @Schema(description = "List of attributes. If this field is provided, all attributes that should be kept must be included here, otherwise they will be removed from the product. For existing attributes, the ID must be provided, otherwise a new attribute will be created.")
        List<@NotNull @Valid ProductAttributeChangeDto> attributes
) {

}
