package ch.portami.inventorybackend.product.dto.productvariant;

import java.math.BigDecimal;

public record ProductVariantPatchRequest(
        String name,
        BigDecimal price
) {

}
