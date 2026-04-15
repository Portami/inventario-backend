package ch.portami.inventorybackend.felt.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreateFeltVariantRequest(
    @NotNull Long feltId,
    @NotNull @Positive Double thickness,
    @NotNull @Positive Double density,
    @NotNull @Positive BigDecimal price
) {

}
