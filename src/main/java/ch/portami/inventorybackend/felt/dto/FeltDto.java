package ch.portami.inventorybackend.felt.dto;

import java.math.BigDecimal;

public record FeltDto(
    Long id,
    String color,
    String supplierColor,
    Double thickness,
    Double density,
    BigDecimal price,
    Long feltVariantId,
    String articleNumber,
    Long supplierId,
    String supplierName,
    Long feltId,
    Long feltTypeId,
    String feltTypeName
) {

}
