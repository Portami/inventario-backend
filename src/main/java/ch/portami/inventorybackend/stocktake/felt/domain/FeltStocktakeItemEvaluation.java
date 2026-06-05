package ch.portami.inventorybackend.stocktake.felt.domain;

import ch.portami.inventorybackend.storage.entity.Storage;
import jakarta.annotation.Nullable;

/**
 * Represents the evaluation of the state of a stocktake item, including whether it has a problem, an applied resolution
 * and more.
 *
 * @param status            the status of the stocktake item.
 * @param needsResolution   whether the item needs a resolution (i.e. has a problem that needs to be resolved).
 * @param resolutionType    the type of resolution that has been applied to the item, if any. Null if the item has no
 *                          problem or if the problem has not been resolved yet.
 * @param mutationApplied   whether the resolution has been applied to the inventory if the stocktake is completed, or
 *                          whether it will be applied once the stocktake is completed.
 * @param newStorage        the new storage assigned to the item by the resolution, if applicable.
 * @param resolutionComment an optional comment about the resolution, e.g. explaining the reason for the resolution or
 *                          providing additional information.
 */
public record FeltStocktakeItemEvaluation(
        FeltStocktakeItemStatus status,
        boolean needsResolution,
        FeltStocktakeResolutionType resolutionType,
        boolean mutationApplied,
        @Nullable Storage newStorage,
        @Nullable String resolutionComment
) {

    /**
     * Creates an evaluation for an item that either has no problem or has a problem that has not been resolved yet.
     *
     * @param status          the status of the item
     * @param needsResolution whether the item needs a resolution
     * @return the created evaluation
     */
    public static FeltStocktakeItemEvaluation createWithoutResolution(FeltStocktakeItemStatus status,
            boolean needsResolution) {
        return new FeltStocktakeItemEvaluation(status, needsResolution, null, false, null, null);
    }

    /**
     * @return whether the item has a problem (i.e. its status is neither OUT_OF_SCOPE, INITIAL nor OK).
     */
    public boolean hasProblem() {
        return status != FeltStocktakeItemStatus.OUT_OF_SCOPE
                && status != FeltStocktakeItemStatus.INITIAL
                && status != FeltStocktakeItemStatus.OK;
    }

    /**
     * @return whether the item has a problem that has been resolved (i.e. it has a resolution type assigned).
     */
    public boolean hasResolvedProblem() {
        return resolutionType != null;
    }

}

