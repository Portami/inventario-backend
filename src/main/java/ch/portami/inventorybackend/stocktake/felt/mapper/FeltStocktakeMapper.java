package ch.portami.inventorybackend.stocktake.felt.mapper;

import ch.portami.inventorybackend.stocktake.felt.dto.stocktake.FeltStocktakeDto;
import ch.portami.inventorybackend.stocktake.felt.dto.stocktake.FeltStocktakeListInfoDto;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktake;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeStorage;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class FeltStocktakeMapper {

    public FeltStocktakeDto toFeltStocktakeDto(FeltStocktake stocktake) {
        List<FeltStocktakeListInfoDto> storageLists = stocktake.getStorages()
                                                               .stream()
                                                               .map(this::toFeltStocktakeListInfoDto)
                                                               .sorted(Comparator.comparing(
                                                                       FeltStocktakeListInfoDto::storageName))
                                                               .toList();

        return new FeltStocktakeDto(
                stocktake.getId(),
                stocktake.getDescription(),
                stocktake.getCreatedAt(),
                storageLists,
                stocktake.getCompletedAt() != null,
                stocktake.getCompletedAt()
        );
    }

    private FeltStocktakeListInfoDto toFeltStocktakeListInfoDto(FeltStocktakeStorage storage) {
        return new FeltStocktakeListInfoDto(storage.getStorage()
                                                   .getId(), storage.getStorage()
                                                                    .getName(), storage.isClosed());
    }

}