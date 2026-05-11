package ch.portami.inventorybackend.offer.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body for creating a customer")
public record CreateCustomerDto(

    @Schema(description = "Name of the customer")
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

