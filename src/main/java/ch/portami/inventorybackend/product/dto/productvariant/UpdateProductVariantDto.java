package ch.portami.inventorybackend.product.dto.productvariant;

import ch.portami.inventorybackend.product.dto.productattributevalue.ProductAttributeValueChangeDto;
import java.math.BigDecimal;
import java.util.List;

public record UpdateProductVariantDto(
        String name,
        BigDecimal price,
        List<ProductAttributeValueChangeDto> attributes
) {

}
