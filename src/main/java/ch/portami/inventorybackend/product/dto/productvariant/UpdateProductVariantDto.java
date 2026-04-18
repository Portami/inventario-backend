package ch.portami.inventorybackend.product.dto.productvariant;

import ch.portami.inventorybackend.product.dto.productattributevalue.ProductAttributeValueChangeDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record UpdateProductVariantDto(
        String name,
        BigDecimal price,
        List<@NotNull @Valid ProductAttributeValueChangeDto> attributes
) {

}
