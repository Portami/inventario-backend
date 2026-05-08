package ch.portami.inventorybackend.offer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record OfferItemDto(

    @Schema(description = "ID of the line item")
    Long id,

    @Schema(description = "ID of the referenced product or product variant")
    Long productVariantId,

    @Schema(description = "Optional free-text description for the line item")
    String description,

    @Schema(description = "Quantity of the item")
    Integer quantity,

    @Schema(description = "Unit price for the item")
    BigDecimal unitPrice

) {

}
