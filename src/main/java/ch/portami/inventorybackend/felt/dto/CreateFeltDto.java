package ch.portami.inventorybackend.felt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateFeltDto(
    @NotNull Long feltTypeId,
    @NotNull Long supplierId,
    @NotBlank String articleNumber
) {

}
