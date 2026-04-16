package ch.portami.inventorybackend.product.dto.productvariant;

import java.math.BigDecimal;

public record ProductVariantRequest(
        String name,
        BigDecimal price
) {

}
