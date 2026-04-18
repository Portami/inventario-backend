package ch.portami.inventorybackend.felt.dto;

import java.math.BigDecimal;

public record FeltRollDto(
    Long id,
    Double length,
    Double width,
    Long feltColorVariantId,
    String color,
    String supplierColor,
    Long feltVariantId,
    Double thickness,
    Double density,
    BigDecimal price,
    Long feltId,
    String articleNumber,
    String feltTypeName,
    String supplierName,
    Long batchId,
    String batchName,
    Long storageId,
    String storageName
) {}
