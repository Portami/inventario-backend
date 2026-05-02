package ch.portami.inventorybackend.product.dto.productvariant;

import ch.portami.inventorybackend.product.dto.productattributevalue.CreateProductAttributeValueDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Request body for creating a new product variant")
public record CreateProductVariantDto(
        @Schema(description = "The name of this variant")
        @NotNull String name,

        @Schema(description = "Sales price of this variant")
        @NotNull @DecimalMin("0.00") BigDecimal price,

        @Schema(description = "List of attributes of this variant and their values")
        List<@NotNull @Valid CreateProductAttributeValueDto> attributes
) {

}
