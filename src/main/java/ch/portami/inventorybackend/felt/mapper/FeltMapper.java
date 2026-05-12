package ch.portami.inventorybackend.felt.mapper;

import ch.portami.inventorybackend.felt.dto.FeltDto;
import ch.portami.inventorybackend.felt.entity.FeltColorVariant;
import ch.portami.inventorybackend.felt.supply.entity.Supply;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FeltMapper {

    @Mapping(source = "feltColorVariant.id", target = "id")
    @Mapping(source = "feltColorVariant.color", target = "color")
    @Mapping(source = "feltColorVariant.supplierColor", target = "supplierColor")
    @Mapping(source = "feltColorVariant.feltVariant.thickness", target = "thickness")
    @Mapping(source = "feltColorVariant.feltVariant.density", target = "density")
    @Mapping(source = "feltColorVariant.feltVariant.price", target = "price")
    @Mapping(source = "feltColorVariant.feltVariant.id", target = "feltVariantId")
    @Mapping(source = "feltColorVariant.feltVariant.felt.articleNumber", target = "articleNumber")
    @Mapping(source = "feltColorVariant.feltVariant.felt.supplier.id", target = "supplierId")
    @Mapping(source = "feltColorVariant.feltVariant.felt.supplier.name", target = "supplierName")
    @Mapping(source = "feltColorVariant.feltVariant.felt.id", target = "feltId")
    @Mapping(source = "feltColorVariant.feltVariant.felt.feltType.id", target = "feltTypeId")
    @Mapping(source = "feltColorVariant.feltVariant.felt.feltType.name", target = "feltTypeName")
    @Mapping(source = "supply.lowOnSupply", target = "lowOnSupply")
    @Mapping(source = "supply.hasBeenReordered", target = "reordered")
    FeltDto toDto(FeltColorVariant feltColorVariant, Supply supply);
}
