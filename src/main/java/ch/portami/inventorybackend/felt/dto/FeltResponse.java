package ch.portami.inventorybackend.felt.dto;

public record FeltResponse(
    Long id,
    String articleNumber,
    Long feltTypeId,
    String feltTypeName,
    Long supplierId,
    String supplierName
) {

}
