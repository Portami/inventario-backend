package ch.portami.inventorybackend.product.dto.product;

import ch.portami.inventorybackend.product.dto.productattribute.CreateProductAttributeDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateProductDto(
        @NotNull String name,
        @NotNull Long categoryId,
        List<@NotNull @Valid CreateProductAttributeDto> attributes
) {

}
