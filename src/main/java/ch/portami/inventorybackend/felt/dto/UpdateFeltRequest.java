package ch.portami.inventorybackend.felt.dto;

public record UpdateFeltRequest(
    Long feltTypeId,
    Long supplierId,
    String articleNumber
) {

}
