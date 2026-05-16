package ch.portami.inventorybackend.felt.mapper;

import ch.portami.inventorybackend.felt.dto.FeltDto;
import ch.portami.inventorybackend.felt.entity.Felt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FeltMapper {

    @Mapping(source = "felt.id", target = "id")
    @Mapping(source = "felt.color", target = "color")
    @Mapping(source = "felt.supplierColor", target = "supplierColor")
    @Mapping(source = "felt.thickness", target = "thickness")
    @Mapping(source = "felt.density", target = "density")
    @Mapping(source = "felt.price", target = "price")
    @Mapping(source = "felt.articleNumber", target = "articleNumber")
    @Mapping(source = "felt.supplier.id", target = "supplierId")
    @Mapping(source = "felt.supplier.name", target = "supplierName")
    @Mapping(source = "felt.feltType.id", target = "feltTypeId")
    @Mapping(source = "felt.feltType.name", target = "feltTypeName")
    @Mapping(source = "felt.lowOnSupply", target = "lowOnSupply")
    @Mapping(source = "felt.hasBeenReordered", target = "reordered")
    FeltDto toDto(Felt felt);
}
