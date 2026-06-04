package ch.portami.inventorybackend.stocktake.felt.domain;

import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeResolutionType;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeItem;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeRollOrScrap;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeScan;
import ch.portami.inventorybackend.storage.entity.Storage;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class FeltStocktakeItemEvaluator {

    public FeltStocktakeItemEvaluation evaluate(FeltStocktakeItem item, boolean stocktakeCompleted,
            boolean expectedStorageClosed, Set<Long> stocktakeStorageIds) {

        Storage expectedStorage = expectedStorage(item);
        List<FeltStocktakeScan> activeScans = item.getScans()
                                                  .stream()
                                                  .filter(s -> !s.isVoided())
                                                  .filter(s -> !s.isCorrected())
                                                  .toList();

        FeltStocktakeItemStatus status = determineStatus(item, activeScans, expectedStorage, expectedStorageClosed);

        FeltStocktakeResolutionType resolutionType = determineResolutionType(item, status);

        if (status == FeltStocktakeItemStatus.WRONG_STORAGE
                && resolutionType == FeltStocktakeResolutionType.MOVE_PHYSICALLY
                && expectedStorage != null && stocktakeStorageIds.contains(expectedStorage.getId())
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
            Storage expectedStorage, boolean expectedStorageClosed) {

        if (activeScans.size() > 1) {
            return FeltStocktakeItemStatus.DUPLICATE_SCAN;
        }

        if (isUnknownItem(item)) {
            return FeltStocktakeItemStatus.UNKNOWN;
        }

        if (expectedStorage == null) {
            return FeltStocktakeItemStatus.NOT_IN_STOCKTAKE;
        }

        if (activeScans.isEmpty()) {
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
            case OK, INITIAL, DUPLICATE_SCAN -> null;
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

        if (status == FeltStocktakeItemStatus.OK || status == FeltStocktakeItemStatus.INITIAL) {
            return false;
        }
        if (status == FeltStocktakeItemStatus.DUPLICATE_SCAN) {
            return true;
        }
        return resolutionType == null;

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

    private boolean isUnknownItem(FeltStocktakeItem item) {
        FeltStocktakeRollOrScrap rollOrScrap = item.getRollOrScrap();
        if (rollOrScrap == null) {
            return true;
        }
        return rollOrScrap.getRoll() == null && rollOrScrap.getScrap() == null;
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