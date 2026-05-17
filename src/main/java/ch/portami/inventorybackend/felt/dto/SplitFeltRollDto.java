package ch.portami.inventorybackend.felt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request body for splitting an existing roll. A new roll is cross-cut from the source.")
public record SplitFeltRollDto(
    @Schema(description = "Width of the new roll to cut from the source roll. Must be positive and less than the source roll's length.", example = "2.0")
    @NotNull @Positive Double width
) {}
