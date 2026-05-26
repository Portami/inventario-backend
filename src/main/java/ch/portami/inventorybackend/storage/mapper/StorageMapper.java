package ch.portami.inventorybackend.storage.mapper;

import ch.portami.inventorybackend.storage.dto.StorageDto;
import ch.portami.inventorybackend.storage.entity.Storage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StorageMapper {

    StorageDto toStorageDto(Storage storage);
}
