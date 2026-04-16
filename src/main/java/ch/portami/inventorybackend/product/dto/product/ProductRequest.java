package ch.portami.inventorybackend.product.dto.product;

import ch.portami.inventorybackend.product.entity.ProductAttribute;
import java.util.List;

public record ProductRequest(
        String name,
        long categoryId,
        List<ProductAttribute> attributes
) {

}
