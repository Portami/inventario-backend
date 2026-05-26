package ch.portami.inventorybackend.product.mapper;

import ch.portami.inventorybackend.product.dto.productattributevalue.ProductAttributeValueDto;
import ch.portami.inventorybackend.product.entity.ProductAttributeValue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductAttributeValueMapper {

    @Mapping(target = "attributeId", source = "productAttribute.id")
    @Mapping(target = "name", source = "productAttribute.name")
    ProductAttributeValueDto toProductAttributeValueDto(ProductAttributeValue productAttributeValue);

}
