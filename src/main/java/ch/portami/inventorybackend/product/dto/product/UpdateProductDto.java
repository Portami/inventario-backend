package ch.portami.inventorybackend.product.dto.product;

import ch.portami.inventorybackend.product.dto.productattribute.ProductAttributeChangeDto;
import java.util.List;

public record UpdateProductDto(
        String name,
        Long categoryId,
        List<ProductAttributeChangeDto> attributes
) {

}
