package ch.portami.inventorybackend.felt.mapper;

import ch.portami.inventorybackend.felt.dto.FeltTypeDto;
import ch.portami.inventorybackend.felt.entity.FeltType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FeltTypeMapper {

    FeltTypeDto toDto(FeltType feltType);
}
