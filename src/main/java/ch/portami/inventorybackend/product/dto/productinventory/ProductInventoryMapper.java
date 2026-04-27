package ch.portami.inventorybackend.product.dto.productinventory;

import ch.portami.inventorybackend.product.entity.ProductInventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductInventoryMapper {

    @Mapping(source = "storage.id", target = "storageId")
    @Mapping(source = "storage.name", target = "storageName")
    ProductInventoryDto toProductInventoryDto(ProductInventory productInventory);

}
