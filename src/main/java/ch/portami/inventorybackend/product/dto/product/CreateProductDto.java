package ch.portami.inventorybackend.product.dto.product;

import ch.portami.inventorybackend.product.dto.productattribute.CreateProductAttributeDto;
import java.util.List;

public record CreateProductDto(
        String name,
        long categoryId,
        List<CreateProductAttributeDto> attributes
) {

}
