package ch.portami.inventorybackend.offer.dto;

import ch.portami.inventorybackend.offer.domain.OfferState;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import java.util.List;

@Schema(description = "An offer - including information about its state and the requesting customer.")
public record OfferDto(

    @Schema(description = "Unique identifier of the offer.")
    Long id,

    @Schema(description = "The customer which the offer is for.")
    CustomerDto customerDto,

    @Schema(description = "The current state of the offer.")
    OfferState state,

    @Schema(description = "The creation date and time of the offer.")
    ZonedDateTime createdAt,

    @Schema(description = "The date and time of the last update on this record.")
    ZonedDateTime updatedAt,

    @Schema(description = "The due date of the offer (payment deadline).")
    ZonedDateTime dueAt,

    @Schema(description = "List of line items included in the offer.")
    List<OfferItemDto> items

) {

}

