package ch.portami.inventorybackend.felt.dto;

import jakarta.validation.constraints.Positive;

public record UpdateFeltRollDto(
    @Positive Double length,
    @Positive Double width,
    Long batchId,
    Long storageId
) {

}
