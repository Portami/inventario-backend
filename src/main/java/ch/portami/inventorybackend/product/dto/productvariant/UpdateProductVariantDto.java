package ch.portami.inventorybackend.product.dto.productvariant;

import java.math.BigDecimal;

public record UpdateProductVariantDto(
        String name,
        BigDecimal price
) {

}
