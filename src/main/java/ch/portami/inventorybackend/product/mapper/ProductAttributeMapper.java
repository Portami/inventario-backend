package ch.portami.inventorybackend.product.mapper;

import ch.portami.inventorybackend.product.dto.productattribute.ProductAttributeDto;
import ch.portami.inventorybackend.product.entity.ProductAttribute;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductAttributeMapper {

    ProductAttributeDto toProductAttributeDto(ProductAttribute productAttribute);

}
