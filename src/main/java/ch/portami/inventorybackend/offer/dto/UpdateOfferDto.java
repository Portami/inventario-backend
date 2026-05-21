package ch.portami.inventorybackend.offer.dto;

import ch.portami.inventorybackend.offer.domain.OfferState;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import java.util.List;

@Schema(description = "Partial update body for an offer. Every field is optional - omit any field (or send it as null) to leave it unchanged.")
public record UpdateOfferDto(

        @Schema(description = "name of the customer which the offer is for")
        String customerName,

        @Schema(description = "current state of the offer")
        OfferState state,

        @Schema(description = "List of line items for the offer (optional)")
        List<UpdateOfferItemDto> items,

        @Schema(description = "Payment due date for the offer (optional)")
        ZonedDateTime dueAt,

        @Schema(description = "Mark whether this offer document has been sent to the customer (optional). Automatically reset to false on state change.")
        Boolean offerSent

) {

}
