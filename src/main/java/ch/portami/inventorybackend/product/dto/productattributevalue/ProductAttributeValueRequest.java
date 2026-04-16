package ch.portami.inventorybackend.product.dto.productattributevalue;

public record ProductAttributeValueRequest(
        long productAttributeId,
        String value
) {

}
