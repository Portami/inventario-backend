package ch.portami.inventorybackend.product.dto.category;

import jakarta.validation.constraints.NotNull;

public record CreateCategoryDto(
        @NotNull String name
) {
}
