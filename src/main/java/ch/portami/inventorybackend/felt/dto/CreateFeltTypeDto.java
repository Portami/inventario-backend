package ch.portami.inventorybackend.felt.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateFeltTypeDto(
    @NotBlank String name
) {

}
