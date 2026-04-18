package ch.portami.inventorybackend.felt.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateFeltRollDto(
    @NotNull @Positive Double length,
    @NotNull @Positive Double width,
    Long batchId,
    Long storageId
) {}
