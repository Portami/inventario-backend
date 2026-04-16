package ch.portami.inventorybackend.felt.dto;

public record UpdateFeltDto(
    Long feltTypeId,
    Long supplierId,
    String articleNumber
) {

}
