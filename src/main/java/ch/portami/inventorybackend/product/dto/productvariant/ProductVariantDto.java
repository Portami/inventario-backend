package ch.portami.inventorybackend.product.dto.productvariant;

import ch.portami.inventorybackend.product.dto.productattributevalue.ProductAttributeValueDto;
import ch.portami.inventorybackend.product.dto.productinventory.ProductInventoryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

public record ProductVariantDto(
        long id,
        String name,
        BigDecimal price,
        List<ProductAttributeValueDto> attributes,

        @Schema(description = "List of inventory entries for this product variant, indicating how many items are available in which storage locations")
        List<ProductInventoryDto> inventory
) {

}
