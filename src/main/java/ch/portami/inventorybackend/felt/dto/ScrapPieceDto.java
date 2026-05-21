package ch.portami.inventorybackend.felt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "A scrap piece of felt, including full felt context and its current batch and storage assignment.")
public record ScrapPieceDto(
        @Schema(description = "Unique scrap piece ID.")
        Long id,

        @Schema(description = "Length of the scrap piece in centimetres.")
        Double length,

        @Schema(description = "Width of the scrap piece in centimetres.")
        Double width,

        @Schema(description = "ID of the felt this scrap piece belongs to.")
        Long feltId,

        @Schema(description = "Color name (e.g. 'Anthracite').")
        String color,

        @Schema(description = "Supplier-side color designation.")
        String supplierColor,

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

        @Schema(description = "ID of the delivery batch. Null if unassigned.", nullable = true)
        Long batchId,

        @Schema(description = "Name of the delivery batch. Null when batchId is null.", nullable = true)
        String batchName,

        @Schema(description = "ID of the storage location. Null if unassigned.", nullable = true)
        Long storageId,

        @Schema(description = "Name of the storage location. Null when storageId is null.", nullable = true)
        String storageName
) {

}
