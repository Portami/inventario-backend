package ch.portami.inventorybackend.offer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

@Schema(description = "Request body for updating an existing offer line item. Fields are optional; only provided fields will be updated.")
public record UpdateOfferItemDto(

    @Schema(description = "ID of the line item to update")
    Long id,

    @Schema(description = "ID of the referenced product or product variant")
    Long productId,

    @Schema(description = "Optional free-text description for the line item")
    String description,

    @Schema(description = "Quantity of the item (must be at least 1 if provided)")
    @Min(1) Integer quantity,

    @Schema(description = "Unit price for the item (>= 0.00 if provided)")
    @DecimalMin("0.00") BigDecimal unitPrice

) {

}

