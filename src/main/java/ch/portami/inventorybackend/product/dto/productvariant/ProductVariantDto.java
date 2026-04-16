package ch.portami.inventorybackend.product.dto.productvariant;

import java.math.BigDecimal;

public record ProductVariantDto(
        long id,
        String name,
        BigDecimal price
) {
}
