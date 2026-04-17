package ch.portami.inventorybackend.product.dto.productvariant;

import java.math.BigDecimal;

public record CreateProductVariantDto(
        String name,
        BigDecimal price
) {

}
