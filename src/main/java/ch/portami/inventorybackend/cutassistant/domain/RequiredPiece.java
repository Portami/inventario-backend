package ch.portami.inventorybackend.cutassistant.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RequiredPiece(
		@NotNull(message = "feltVariantId must not be null") Long feltVariantId,
		@NotBlank(message = "color must not be blank") String color,
		@NotNull(message = "length must not be null") @Positive(message = "length must be positive") Double length,
		@NotNull(message = "width must not be null") @Positive(message = "width must be positive") Double width,
		@NotNull(message = "quantity must not be null") @Positive(message = "quantity must be positive") Integer quantity
) {

}

