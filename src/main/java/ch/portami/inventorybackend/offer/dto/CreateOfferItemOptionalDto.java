package ch.portami.inventorybackend.offer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Request body for creating a new (possibly empty) line item inside an existing offer")
public record CreateOfferItemOptionalDto(

    @Schema(description = "ID of the referenced product or product variant")
    Long productVariantId,

    @Schema(description = "Optional free-text description for the line item")
    String description,

    @Schema(description = "Quantity of the item (may be null when creating an empty line)")
    Integer quantity,

    @Schema(description = "Unit price for the item (>= 0.00)")
    BigDecimal unitPrice

) {

}

