package ch.portami.inventorybackend.felt.dto;

public record FeltDto(
    Long id,
    String articleNumber,
    Long feltTypeId,
    String feltTypeName,
    Long supplierId,
    String supplierName
) {

}
