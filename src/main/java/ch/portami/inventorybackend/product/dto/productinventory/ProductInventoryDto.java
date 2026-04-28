package ch.portami.inventorybackend.product.dto.productinventory;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Type representing the amount of a product variant available in a specific storage location.")
public record ProductInventoryDto(
        @Schema(description = "The ID of the storage this entry belongs to")
        long storageId,

        @Schema(description = "The name of the storage this entry belongs to")
        String storageName,

        @Schema(description = "The amount of the product variant available in this storage")
        int quantity
) {

}
