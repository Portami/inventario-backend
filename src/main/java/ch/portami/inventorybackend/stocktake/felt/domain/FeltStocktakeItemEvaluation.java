package ch.portami.inventorybackend.stocktake.felt.domain;

import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeItemStatus;
import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeResolutionType;
import ch.portami.inventorybackend.storage.entity.Storage;
import jakarta.annotation.Nullable;

public record FeltStocktakeItemEvaluation(
        FeltStocktakeItemStatus status,
        boolean needsResolution,
        FeltStocktakeResolutionType resolutionType,
        boolean mutationOutsideStocktake,
        boolean mutationApplied,
        @Nullable Storage newStorage,
        @Nullable String resolutionComment
) {

    public static FeltStocktakeItemEvaluation createWithoutResolution(FeltStocktakeItemStatus status,
            boolean needsResolution) {
        return new FeltStocktakeItemEvaluation(status, needsResolution, null, false, false, null, null);
    }

    public boolean hasProblem() {
        return status != FeltStocktakeItemStatus.OK && status != FeltStocktakeItemStatus.INITIAL;
    }

    public boolean hasResolvedProblem() {
        return resolutionType != null;
    }
}

