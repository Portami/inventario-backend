package ch.portami.inventorybackend.stocktake.felt.dto.item;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record FeltStocktakeRollOrScrapDto(
        @Schema(description = "Unique roll or scrap ID. Is null if the item has been deleted during or after the stocktake process.", nullable = true)
        Long id,

        @Schema(description = "Length of the roll or scrap piece in metres.")
        Double length,

        @Schema(description = "Width of the roll or scrap piece in metres.")
        Double width,

        @Schema(description = "ID of the felt this item belongs to. Is null if the item has been deleted during or after the stocktake process.", nullable = true)
        Long feltId,

        @Schema(description = "Color name.")
        String color,

        @Schema(description = "Thickness in millimetres.")
        Double thickness,

        @Schema(description = "Density in grams per square metre.")
        Double density,

        @Schema(description = "Purchase price per unit.")
        BigDecimal price,

        @Schema(description = "Supplier article number.")
        String articleNumber,

        @Schema(description = "Felt type name (e.g. 'Wool', 'Synthetic').")
        String feltTypeName,

        @Schema(description = "Display name of the supplier.")
        String supplierName,

        @Schema(description = "ID of the expected storage location.")
        Long expectedStorageId,

        @Schema(description = "Name of the expected storage location.")
        String expectedStorageName
) {

}
