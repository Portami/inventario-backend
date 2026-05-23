package ch.portami.inventorybackend.offer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "Request body for creating a new line item inside an offer")
public record CreateOfferItemDto(

        @Schema(description = "ID of the referenced product or product variant")
        @NotNull Long productVariantId,

        @Schema(description = "Optional free-text description for the line item")
        String description,

        @Schema(description = "Quantity of the item (must be at least 1)")
        @NotNull @Min(1) Integer quantity,

        @Schema(description = "Unit price for the item (>= 0.00)")
        @NotNull @DecimalMin("0.00") BigDecimal unitPrice

) {

}

