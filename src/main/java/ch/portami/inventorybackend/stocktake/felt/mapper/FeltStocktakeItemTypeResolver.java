package ch.portami.inventorybackend.stocktake.felt.mapper;

import ch.portami.inventorybackend.stocktake.felt.dto.FeltStocktakeItemType;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeItem;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeRollOrScrap;

public class FeltStocktakeItemTypeResolver {

    private FeltStocktakeItemTypeResolver() {
    }

    public static FeltStocktakeItemType resolve(FeltStocktakeItem item) {

        FeltStocktakeRollOrScrap rollOrScrap = item.getRollOrScrap();

        if (rollOrScrap == null) {
            return FeltStocktakeItemType.UNKNOWN;
        }

        return switch (rollOrScrap.getType()) {
            case ROLL -> FeltStocktakeItemType.ROLL;
            case SCRAP -> FeltStocktakeItemType.SCRAP;
        };

    }

}
