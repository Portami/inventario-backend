package ch.portami.inventorybackend.product.dto.product;

import ch.portami.inventorybackend.product.dto.category.CategoryDto;
import ch.portami.inventorybackend.product.dto.productattribute.ProductAttributeDto;
import ch.portami.inventorybackend.product.dto.productvariant.ProductVariantDto;
import java.util.List;

public record ProductDto(
        long id,
        String name,
        CategoryDto category,
        List<ProductVariantDto> variants,
        List<ProductAttributeDto> attributes
) {

}
