package ch.portami.inventorybackend.stocktake.felt.mapper;

import ch.portami.inventorybackend.stocktake.felt.domain.FeltStocktakeItemStatus;
import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeItemApiStatus;

public class FeltStocktakeItemApiStatusMapper {

    private FeltStocktakeItemApiStatusMapper() {
    }

    public static FeltStocktakeItemApiStatus toApiStatus(FeltStocktakeItemStatus status) {
        return switch (status) {
            case INITIAL -> FeltStocktakeItemApiStatus.INITIAL;
            case OK -> FeltStocktakeItemApiStatus.OK;
            case MISSING -> FeltStocktakeItemApiStatus.MISSING;
            case WRONG_STORAGE -> FeltStocktakeItemApiStatus.WRONG_STORAGE;
            case RESCAN_REQUIRED -> FeltStocktakeItemApiStatus.RESCAN_REQUIRED;
            case DUPLICATE_SCAN -> FeltStocktakeItemApiStatus.DUPLICATE_SCAN;
            case NOT_IN_STOCKTAKE -> FeltStocktakeItemApiStatus.NOT_IN_STOCKTAKE;
            case UNKNOWN -> FeltStocktakeItemApiStatus.UNKNOWN;
        };
    }

}
