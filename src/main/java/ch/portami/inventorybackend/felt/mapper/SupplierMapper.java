package ch.portami.inventorybackend.felt.mapper;

import ch.portami.inventorybackend.felt.dto.SupplierDto;
import ch.portami.inventorybackend.felt.entity.Supplier;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    SupplierDto toDto(Supplier supplier);
}
