package ch.portami.inventorybackend.product.dto.productvariant;

import ch.portami.inventorybackend.product.dto.productattributevalue.ProductAttributeValueChangeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = """
        Partial update body for a product variant. Every field is optional — omit any field \
        (or send it as null) to leave it unchanged. Only the fields you provide are applied. \
        If the attributes field is provided, it will replace the entire list of attributes of the variant. \
        To keep existing attributes, they must be included in the list. To remove an attribute, simply omit it from the list.\
        """)
public record UpdateProductVariantDto(
        @Schema(description = "New name of this variant")
        String name,

        @Schema(description = "New sales price of this variant")
        @DecimalMin("0.00") BigDecimal price,

        @Schema(description = "List of attributes of this variant and their new values. If this field is provided, all attributes that should be kept must be included here, otherwise they will be removed from the variant.")
        List<@NotNull @Valid ProductAttributeValueChangeDto> attributes
) {

}
