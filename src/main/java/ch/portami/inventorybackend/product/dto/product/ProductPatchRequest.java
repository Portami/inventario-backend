package ch.portami.inventorybackend.product.dto.product;

import ch.portami.inventorybackend.product.dto.productattribute.ProductAttributeRequest;
import ch.portami.inventorybackend.product.entity.ProductAttribute;
import java.util.List;

public record ProductPatchRequest(
        String name,
        Long categoryId,
        List<ProductAttributeRequest> attributes
) {

}
