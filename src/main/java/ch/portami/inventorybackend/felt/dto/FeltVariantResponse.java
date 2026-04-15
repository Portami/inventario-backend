package ch.portami.inventorybackend.felt.dto;

import java.math.BigDecimal;

public record FeltVariantResponse(
    Long id,
    Long feltId,
    String articleNumber,
    String feltTypeName,
    String supplierName,
    Double thickness,
    Double density,
    BigDecimal price
) {

}
