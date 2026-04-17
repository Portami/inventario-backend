package ch.portami.inventorybackend.product.dto.product;

import ch.portami.inventorybackend.product.entity.ProductAttribute;
import java.util.List;

public record CreateProductDto(
        String name,
        long categoryId,
        List<ProductAttribute> attributes
) {

}
