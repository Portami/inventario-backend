package ch.portami.inventorybackend.felt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "A physical roll of felt, including full context from the felt hierarchy and its current batch and storage assignment.")
public record FeltRollDto(
    @Schema(description = "Unique roll ID.")
    Long id,

    @Schema(description = "Length of the roll in metres.")
    Double length,

    @Schema(description = "Width of the roll in metres.")
    Double width,

    @Schema(description = "ID of the felt color variant this roll belongs to. Matches the feltId used in POST /api/rolls and GET /api/felts/{feltId}/rolls.")
    Long feltColorVariantId,

    @Schema(description = "Color name of the felt color variant (e.g. 'Anthracite').")
    String color,

    @Schema(description = "Supplier-side color designation.")
    String supplierColor,

    @Schema(description = "Internal FeltVariant ID — provided for reference only.")
    Long feltVariantId,

    @Schema(description = "Thickness of the felt in millimetres.")
    Double thickness,

    @Schema(description = "Density of the felt in grams per square metre.")
    Double density,

    @Schema(description = "Purchase price per unit.")
    BigDecimal price,

    @Schema(description = "Internal Felt entity ID — provided for reference only.")
    Long feltId,

    @Schema(description = "Supplier article number.")
    String articleNumber,

    @Schema(description = "Felt type name (e.g. 'Wool', 'Synthetic').")
    String feltTypeName,

    @Schema(description = "Display name of the supplier.")
    String supplierName,

    @Schema(description = "ID of the delivery batch this roll arrived in. Null if the roll has not been assigned to a batch.", nullable = true)
    Long batchId,

    @Schema(description = "Name of the delivery batch. Null when batchId is null.", nullable = true)
    String batchName,

    @Schema(description = "ID of the storage location where this roll is physically kept. Null if unassigned.", nullable = true)
    Long storageId,

    @Schema(description = "Name of the storage location. Null when storageId is null.", nullable = true)
    String storageName,

    @Schema(description = "Whether this roll is flagged as low on supply and needs reordering.")
    boolean lowOnSupply,

    @Schema(description = "Whether a reorder for this roll is already in process.")
    boolean reordered
) {}
