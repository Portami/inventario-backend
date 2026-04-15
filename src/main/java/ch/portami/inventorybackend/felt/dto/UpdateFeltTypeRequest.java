package ch.portami.inventorybackend.felt.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateFeltTypeRequest(
    @NotBlank String name
) {

}
