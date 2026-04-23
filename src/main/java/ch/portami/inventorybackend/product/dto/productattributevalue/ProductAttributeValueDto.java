package ch.portami.inventorybackend.product.dto.productattributevalue;

public record ProductAttributeValueDto(
        long attributeId,
        String name,
        String value
) {

}
