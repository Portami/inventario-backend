package ch.portami.inventorybackend.felt.mapper;

import ch.portami.inventorybackend.felt.dto.ScrapPieceDto;
import ch.portami.inventorybackend.felt.entity.ScrapPiece;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScrapPieceMapper {

    @Mapping(source = "feltColorVariant.id", target = "feltColorVariantId")
    @Mapping(source = "feltColorVariant.color", target = "color")
    @Mapping(source = "feltColorVariant.supplierColor", target = "supplierColor")
    @Mapping(source = "feltColorVariant.feltVariant.id", target = "feltVariantId")
    @Mapping(source = "feltColorVariant.feltVariant.thickness", target = "thickness")
    @Mapping(source = "feltColorVariant.feltVariant.density", target = "density")
    @Mapping(source = "feltColorVariant.feltVariant.price", target = "price")
    @Mapping(source = "feltColorVariant.feltVariant.felt.id", target = "feltId")
    @Mapping(source = "feltColorVariant.feltVariant.felt.articleNumber", target = "articleNumber")
    @Mapping(source = "feltColorVariant.feltVariant.felt.feltType.name", target = "feltTypeName")
    @Mapping(source = "feltColorVariant.feltVariant.felt.supplier.name", target = "supplierName")
    @Mapping(source = "batch.id", target = "batchId")
    @Mapping(source = "batch.name", target = "batchName")
    @Mapping(source = "storage.id", target = "storageId")
    @Mapping(source = "storage.name", target = "storageName")
    ScrapPieceDto toDto(ScrapPiece scrapPiece);
}
