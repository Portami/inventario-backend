package ch.portami.inventorybackend.products.dto;

import ch.portami.inventorybackend.products.model.Color;
import ch.portami.inventorybackend.products.model.ProductType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Payload for creating or fully replacing a product.
 */
public record ProductRequest(

        String name,

        @NotBlank(message = "Article number must not be blank")
        String articleNumber,

        @NotNull(message = "Product type must not be null")
        ProductType type,

        @NotNull(message = "Color must not be null")
        Color color,

        Integer thickness,

        Integer density
) {}
