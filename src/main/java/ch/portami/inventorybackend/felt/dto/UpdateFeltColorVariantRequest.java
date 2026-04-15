package ch.portami.inventorybackend.felt.dto;

public record UpdateFeltColorVariantRequest(
    String color,
    String supplierColor
) {

}
