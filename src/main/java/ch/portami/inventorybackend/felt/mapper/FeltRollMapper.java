package ch.portami.inventorybackend.felt.mapper;

import ch.portami.inventorybackend.felt.dto.FeltRollDto;
import ch.portami.inventorybackend.felt.entity.FeltRoll;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FeltRollMapper {

    @Mapping(source = "felt.id", target = "feltId")
    @Mapping(source = "felt.color", target = "color")
    @Mapping(source = "felt.supplierColor", target = "supplierColor")
    @Mapping(source = "felt.thickness", target = "thickness")
    @Mapping(source = "felt.density", target = "density")
    @Mapping(source = "felt.price", target = "price")
    @Mapping(source = "felt.articleNumber", target = "articleNumber")
    @Mapping(source = "felt.feltType.name", target = "feltTypeName")
    @Mapping(source = "felt.supplier.name", target = "supplierName")
    @Mapping(source = "batch.id", target = "batchId")
    @Mapping(source = "batch.name", target = "batchName")
    @Mapping(source = "storage.id", target = "storageId")
    @Mapping(source = "storage.name", target = "storageName")
    FeltRollDto toDto(FeltRoll roll);
}
