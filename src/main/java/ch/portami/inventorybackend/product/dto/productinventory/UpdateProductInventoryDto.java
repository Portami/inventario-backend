package ch.portami.inventorybackend.product.dto.productinventory;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Type representing a wanted change of the inventory of a product variant in a specific storage location. It contains the ID of the product variant, the ID of the storage, and the amount by which the inventory should be changed (positive for increase, negative for decrease).")
public record UpdateProductInventoryDto(
        @Schema(description = "The ID of the product variant whose inventory is to be changed")
        @NotNull Long productVariantId,

        @Schema(description = "The ID of the storage location where the inventory change should be applied")
        @NotNull Long storageId,

        @Schema(description = "The amount by which to change the inventory (positive to increase, negative to decrease)")
        @NotNull Integer quantityChange
) {

}
