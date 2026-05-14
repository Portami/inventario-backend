package ch.portami.inventorybackend.offer.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Partial update body for a customer. All fields are optional.")
public record UpdateCustomerDto(

    String name,
    String contactPerson,
    String email,
    String phone,
    String street,
    String zip,
    String city,
    String country,
    String vatNumber

) {

}

