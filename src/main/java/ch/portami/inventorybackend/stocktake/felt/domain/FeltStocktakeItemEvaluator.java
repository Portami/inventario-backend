package ch.portami.inventorybackend.stocktake.felt.domain;

import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeItem;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeRollOrScrap;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeScan;
import ch.portami.inventorybackend.storage.entity.Storage;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * Evaluates the status of felt stocktake items.
 */
@Component
public class FeltStocktakeItemEvaluator {

    /**
     * Evaluates the status of a felt stocktake item based on its scans, expected storage, and the states of storages.
     *
     * @param item               the felt stocktake item to evaluate
     * @param stocktakeCompleted whether the stocktake has been completed
     * @param storageStates      a map of storage IDs to their closed/open state
     * @return an evaluation containing information about the items current state within the stocktake
     */
    public FeltStocktakeItemEvaluation evaluate(FeltStocktakeItem item, boolean stocktakeCompleted,
            Map<Long, Boolean> storageStates) {

        Storage expectedStorage = expectedStorage(item);
        List<FeltStocktakeScan> activeScans = item.getScans()
                                                  .stream()
                                                  .filter(s -> !s.isVoided())
                                                  .filter(s -> !s.isCorrected())
                                                  .toList();

        FeltStocktakeItemStatus status = determineStatus(item, activeScans, expectedStorage, storageStates);

        FeltStocktakeResolutionType resolutionType = determineResolutionType(item, status);

        if (status == FeltStocktakeItemStatus.WRONG_STORAGE
                && resolutionType == FeltStocktakeResolutionType.MOVE_PHYSICALLY
                && expectedStorage != null && storageStates.containsKey(expectedStorage.getId())
                && activeScans.stream()
                              .noneMatch(scan -> isExpectedStorage(scan, expectedStorage))) {
            status = FeltStocktakeItemStatus.RESCAN_REQUIRED;
        }

        boolean needsResolution = needsResolution(status, resolutionType);

        if (resolutionType == null) {
            return FeltStocktakeItemEvaluation.createWithoutResolution(status, needsResolution);
        }

        return createEvaluationWithResolution(item, status, resolutionType, stocktakeCompleted, expectedStorage);
    }

    private FeltStocktakeItemStatus determineStatus(FeltStocktakeItem item, List<FeltStocktakeScan> activeScans,
            Storage expectedStorage, Map<Long, Boolean> storageStates) {

        if (activeScans.size() > 1) {
            return FeltStocktakeItemStatus.DUPLICATE_SCAN;
        }

        if (item.getRollOrScrap() == null) {
            return FeltStocktakeItemStatus.UNKNOWN;
        }

        if (expectedStorage == null) {
            return FeltStocktakeItemStatus.NOT_IN_STOCKTAKE;
        }

        if (activeScans.isEmpty()) {
            Boolean expectedStorageClosed = storageStates.get(expectedStorage.getId());

            if (expectedStorageClosed == null) {
                return FeltStocktakeItemStatus.OUT_OF_SCOPE;
            }

            return expectedStorageClosed ? FeltStocktakeItemStatus.MISSING : FeltStocktakeItemStatus.INITIAL;
        }

        FeltStocktakeScan scan = activeScans.getFirst();

        if (isExpectedStorage(scan, expectedStorage) && !item.isProblemAcknowledged()) {
            return FeltStocktakeItemStatus.OK;
        }

        return FeltStocktakeItemStatus.WRONG_STORAGE;
    }

    private FeltStocktakeResolutionType determineResolutionType(FeltStocktakeItem item,
            FeltStocktakeItemStatus status) {

        if (!item.isProblemAcknowledged()) {
            return null;
        }

        return switch (status) {
            case OK, INITIAL, OUT_OF_SCOPE, DUPLICATE_SCAN -> null;
            case WRONG_STORAGE -> item.isMutationWanted()
                    ? FeltStocktakeResolutionType.ADJUST_STORAGE
                    : FeltStocktakeResolutionType.MOVE_PHYSICALLY;
            case RESCAN_REQUIRED -> FeltStocktakeResolutionType.MOVE_PHYSICALLY;
            case MISSING -> item.isMutationWanted()
                    ? FeltStocktakeResolutionType.REMOVE_MISSING
                    : FeltStocktakeResolutionType.IGNORE_MISSING;
            case NOT_IN_STOCKTAKE, UNKNOWN -> FeltStocktakeResolutionType.ACKNOWLEDGE;
        };

    }

    private boolean needsResolution(FeltStocktakeItemStatus status, FeltStocktakeResolutionType resolutionType) {
        return switch (status) {
            case OUT_OF_SCOPE, INITIAL, OK -> false;
            case DUPLICATE_SCAN -> true;
            default -> resolutionType == null;
        };
    }

    private FeltStocktakeItemEvaluation createEvaluationWithResolution(FeltStocktakeItem item,
            FeltStocktakeItemStatus status, FeltStocktakeResolutionType resolutionType, boolean stocktakeCompleted,
            Storage expectedStorage) {

        boolean mutationApplied = stocktakeCompleted
                ? item.isMutationApplied()
                : willApplyMutation(item, resolutionType);

        Storage newStorage = null;
        if (resolutionType == FeltStocktakeResolutionType.ADJUST_STORAGE) {
            newStorage = item.getNewStorage();
        } else if (resolutionType == FeltStocktakeResolutionType.MOVE_PHYSICALLY) {
            newStorage = expectedStorage;
        }

        return new FeltStocktakeItemEvaluation(
                status,
                false,
                resolutionType,
                mutationApplied,
                newStorage,
                item.getResolutionComment()
        );
    }

    private boolean willApplyMutation(FeltStocktakeItem item, FeltStocktakeResolutionType resolutionType) {

        if (resolutionType != FeltStocktakeResolutionType.ADJUST_STORAGE
                && resolutionType != FeltStocktakeResolutionType.REMOVE_MISSING) {
            return false;
        }

        FeltStocktakeRollOrScrap rollOrScrap = item.getRollOrScrap();

        if (rollOrScrap == null) {
            return false;
        }

        return rollOrScrap.getRoll() != null || rollOrScrap.getScrap() != null;

    }

    private Storage expectedStorage(FeltStocktakeItem item) {
        FeltStocktakeRollOrScrap rollOrScrap = item.getRollOrScrap();
        if (rollOrScrap == null) {
            return null;
        }
        return rollOrScrap.getExpectedStorage();
    }

    private boolean isExpectedStorage(FeltStocktakeScan scan, Storage expectedStorage) {
        return Objects.equals(scan.getScannedStorage(), expectedStorage);
    }

}