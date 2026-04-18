package ch.portami.inventorybackend.felt.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateFeltRollDto(
    @NotNull Long feltId,
    @NotNull @Positive Double length,
    @NotNull @Positive Double width,
    Long batchId,
    Long storageId
) {}
