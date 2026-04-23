package ch.portami.inventorybackend.product.dto.productvariant;

import ch.portami.inventorybackend.product.dto.productattributevalue.ProductAttributeValueDto;
import java.math.BigDecimal;
import java.util.List;

public record ProductVariantDto(
        long id,
        String name,
        BigDecimal price,
        List<ProductAttributeValueDto> attributes
) {

}
