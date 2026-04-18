package ch.portami.inventorybackend.product.dto.product;

import ch.portami.inventorybackend.product.dto.productattribute.ProductAttributeChangeDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record UpdateProductDto(
        String name,
        Long categoryId,
        List<@NotNull @Valid ProductAttributeChangeDto> attributes
) {

}
