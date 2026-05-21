package ch.portami.inventorybackend.offer.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A customer - including contact and address information.")
public record CustomerDto(

        @Schema(description = "Unique identifier of the customer.")
        Long id,

        @Schema(description = "name of the customer")
        String name,

        @Schema(description = "Contact person at the customer")
        String contactPerson,

        @Schema(description = "Email address")
        String email,

        @Schema(description = "Phone number")
        String phone,

        @Schema(description = "Street")
        String street,

        @Schema(description = "ZIP / postal code")
        String zip,

        @Schema(description = "City")
        String city,

        @Schema(description = "Country")
        String country,

        @Schema(description = "VAT / UID number")
        String vatNumber

) {

}

