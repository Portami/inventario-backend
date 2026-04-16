package ch.portami.inventorybackend.product.dto.productattribute;

import ch.portami.inventorybackend.product.entity.ProductAttribute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductAttributeMapper {

    ProductAttributeDto toProductAttributeDto(ProductAttribute productAttribute);

}
