package ch.portami.inventorybackend.felt.mapper;

import ch.portami.inventorybackend.felt.dto.BatchDto;
import ch.portami.inventorybackend.felt.entity.Batch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BatchMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    BatchDto toDto(Batch batch);
}
