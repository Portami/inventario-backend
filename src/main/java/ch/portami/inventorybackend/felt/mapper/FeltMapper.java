package ch.portami.inventorybackend.felt.mapper;

import ch.portami.inventorybackend.felt.dto.FeltDto;
import ch.portami.inventorybackend.felt.entity.FeltColorVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FeltMapper {

    @Mapping(source = "feltVariant.thickness", target = "thickness")
    @Mapping(source = "feltVariant.density", target = "density")
    @Mapping(source = "feltVariant.price", target = "price")
    @Mapping(source = "feltVariant.id", target = "feltVariantId")
    @Mapping(source = "feltVariant.felt.articleNumber", target = "articleNumber")
    @Mapping(source = "feltVariant.felt.supplier.id", target = "supplierId")
    @Mapping(source = "feltVariant.felt.supplier.name", target = "supplierName")
    @Mapping(source = "feltVariant.felt.id", target = "feltId")
    @Mapping(source = "feltVariant.felt.feltType.id", target = "feltTypeId")
    @Mapping(source = "feltVariant.felt.feltType.name", target = "feltTypeName")
    @Mapping(target = "lowOnSupply", constant = "false")
    @Mapping(target = "reordered", constant = "false")
    FeltDto toDto(FeltColorVariant feltColorVariant);
}
