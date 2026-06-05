package ch.portami.inventorybackend.stocktake.felt.mapper;

import ch.portami.inventorybackend.stocktake.felt.domain.FeltStocktakeItemEvaluation;
import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeResolutionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FeltStocktakeResolutionMapper {

    @Mapping(target = "newStorageId", source = "newStorage.id")
    @Mapping(target = "newStorageName", source = "newStorage.name")
    @Mapping(target = "resolution", source = "resolutionType")
    @Mapping(target = "comment", source = "resolutionComment")
    FeltStocktakeResolutionDto toDto(FeltStocktakeItemEvaluation evaluation);

}
