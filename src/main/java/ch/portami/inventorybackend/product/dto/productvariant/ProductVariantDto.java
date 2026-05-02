package ch.portami.inventorybackend.product.dto.productvariant;

import ch.portami.inventorybackend.product.dto.productattributevalue.ProductAttributeValueDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "A variant of a product, which can differ in price and attribute values from other variants of the same product")
public record ProductVariantDto(
        @Schema(description = "Product variant ID")
        long id,

        @Schema(description = "Name of this variant")
        String name,

        @Schema(description = "Sales price of this variant")
        BigDecimal price,

        @Schema(description = "List of attributes of this variant and their values")
        List<ProductAttributeValueDto> attributes
) {

}
