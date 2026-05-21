package ch.portami.inventorybackend.offer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Schema(description = "Request body for creating a new felt offer.")
public record CreateOfferDto(

        @Schema(description = "name of the customer which the offer is for")
        @NotBlank String customerName,

        @Schema(description = "List of line items for the offer")
        List<CreateOfferItemDto> items

) {

}

