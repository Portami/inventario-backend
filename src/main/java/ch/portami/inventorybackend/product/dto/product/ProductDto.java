package ch.portami.inventorybackend.product.dto.product;

import ch.portami.inventorybackend.product.dto.category.CategoryDto;
import ch.portami.inventorybackend.product.dto.productattribute.ProductAttributeDto;
import ch.portami.inventorybackend.product.dto.productvariant.ProductVariantDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "A product with its category, variants, and attributes")
public record ProductDto(
        @Schema(description = "Product ID")
        Long id,

        @Schema(description = "Product name")
        String name,

        @Schema(description = "Category of this product")
        CategoryDto category,

        @Schema(description = "List of variants of this product")
        List<ProductVariantDto> variants,

        @Schema(description = "List of attributes of this product")
        List<ProductAttributeDto> attributes
) {

}
