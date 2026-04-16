package ch.portami.inventorybackend.felt.dto;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record UpdateFeltVariantDto(
    @Positive Double thickness,
    @Positive Double density,
    @Positive BigDecimal price
) {

}
