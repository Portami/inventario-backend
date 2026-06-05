package ch.portami.inventorybackend.stocktake.felt.mapper;

import ch.portami.inventorybackend.stocktake.felt.dto.scan.FeltStocktakeScanDto;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeItem;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeScan;
import org.springframework.stereotype.Component;

@Component
public class FeltStocktakeScanMapper {

    public FeltStocktakeScanDto toDto(FeltStocktakeScan scan) {
        FeltStocktakeItem item = scan.getStocktakeItem();
        return new FeltStocktakeScanDto(
                scan.getId(),
                FeltStocktakeItemTypeResolver.resolve(item),
                item.getId(),
                scan.getBarcode(),
                scan.getScannedStorage()
                    .getId(),
                scan.isVoided(),
                scan.isCorrected(),
                scan.getScannedAt()
        );
    }

}