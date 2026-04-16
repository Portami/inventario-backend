package ch.portami.inventorybackend.product.dto.productattributevalue;

import ch.portami.inventorybackend.product.entity.ProductAttributeValue;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductAttributeValueMapper {

    ProductAttributeValueDto toProductAttributeValueDto(ProductAttributeValue productAttributeValue);

}
