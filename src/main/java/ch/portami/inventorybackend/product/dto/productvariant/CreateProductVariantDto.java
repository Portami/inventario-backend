package ch.portami.inventorybackend.product.dto.productvariant;

import ch.portami.inventorybackend.product.dto.productattributevalue.CreateProductAttributeValueDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record CreateProductVariantDto(
        @NotNull String name,
        @NotNull BigDecimal price,
        List<@NotNull @Valid CreateProductAttributeValueDto> attributes
) {

}
