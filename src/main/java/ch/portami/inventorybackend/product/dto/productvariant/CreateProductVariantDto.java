package ch.portami.inventorybackend.product.dto.productvariant;

import ch.portami.inventorybackend.product.dto.productattributevalue.CreateProductAttributeValueDto;
import java.math.BigDecimal;
import java.util.List;

public record CreateProductVariantDto(
        String name,
        BigDecimal price,
        List<CreateProductAttributeValueDto> attributes
) {

}
