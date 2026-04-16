package ch.portami.inventorybackend.felt.dto;

public record FeltColorVariantDto(
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
