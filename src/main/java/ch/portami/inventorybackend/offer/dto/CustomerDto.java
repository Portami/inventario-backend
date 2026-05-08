package ch.portami.inventorybackend.offer.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A customer - including only the most basic information for display purposes in the offer overview.")
public record CustomerDto(

    @Schema(description = "Unique identifier of the customer.")
    Long id,

    @Schema(description = "name of the customer")
    String name

) {

}

