package ch.portami.inventorybackend.stocktake.felt.domain;

import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeItemStatus;
import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeResolutionDto;
import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeResolutionType;

public record FeltStocktakeItemEvaluation(
        FeltStocktakeItemStatus status,
        boolean needsResolution,
        FeltStocktakeResolutionType resolutionType,
        FeltStocktakeResolutionDto resolutionDto
) {

    public boolean hasProblem() {
        return status != FeltStocktakeItemStatus.OK && status != FeltStocktakeItemStatus.INITIAL;
    }
    
    public boolean hasResolvedProblem() {
        return resolutionType != null;
    }
}

