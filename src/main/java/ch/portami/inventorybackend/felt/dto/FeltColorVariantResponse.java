package ch.portami.inventorybackend.felt.dto;

public record FeltColorVariantResponse(
    Long id,
    String color,
    String supplierColor,
    Long feltVariantId,
    Long feltId,
    String articleNumber,
    String feltTypeName,
    String supplierName
) {

}
