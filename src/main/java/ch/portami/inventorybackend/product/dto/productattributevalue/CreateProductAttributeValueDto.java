package ch.portami.inventorybackend.product.dto.productattributevalue;

public record CreateProductAttributeValueDto(
        long productAttributeId,
        String value
) {

}
