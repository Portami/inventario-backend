package ch.portami.inventorybackend.stocktake.felt.domain;

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
        if (rollOrScrap.getRoll() != null) {
            return FeltStocktakeItemType.ROLL;
        }
        if (rollOrScrap.getScrap() != null) {
            return FeltStocktakeItemType.SCRAP;
        }
        return FeltStocktakeItemType.UNKNOWN;
    }
}
