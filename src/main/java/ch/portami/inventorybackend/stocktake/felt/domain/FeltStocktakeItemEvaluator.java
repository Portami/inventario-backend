package ch.portami.inventorybackend.stocktake.felt.domain;

import ch.portami.inventorybackend.core.storage.entity.Storage;
import ch.portami.inventorybackend.felt.entity.FeltRoll;
import ch.portami.inventorybackend.felt.entity.ScrapPiece;
import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeItemStatus;
import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeResolutionDto;
import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeResolutionType;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeItem;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeRollOrScrap;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeScan;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class FeltStocktakeItemEvaluator {

    public FeltStocktakeItemEvaluation evaluate(FeltStocktakeItem item, List<FeltStocktakeScan> scans,
            boolean stocktakeCompleted, boolean expectedStorageClosed, Set<Long> stocktakeStorageIds) {

        Storage expectedStorage = expectedStorage(item);
        List<FeltStocktakeScan> activeScans = scans.stream()
                                                   .filter(FeltStocktakeScan::isVoided)
                                                   .filter(FeltStocktakeScan::isCorrected)
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

        FeltStocktakeResolutionDto resolutionDto = toResolutionDto(item, resolutionType, expectedStorage,
                stocktakeCompleted);

        return new FeltStocktakeItemEvaluation(status, needsResolution, resolutionType, resolutionDto);
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

        if (isExpectedStorage(scan, expectedStorage)) {
            return FeltStocktakeItemStatus.OK;
        }

        return FeltStocktakeItemStatus.WRONG_STORAGE;
    }

    private FeltStocktakeResolutionType determineResolutionType(FeltStocktakeItem item,
            FeltStocktakeItemStatus status) {

        return switch (status) {
            case OK, INITIAL, DUPLICATE_SCAN -> null;
            case WRONG_STORAGE -> item.isMutationWanted()
                    ? FeltStocktakeResolutionType.ADJUST_STORAGE
                    : FeltStocktakeResolutionType.MOVE_PHYSICALLY;
            case RESCAN_REQUIRED -> FeltStocktakeResolutionType.MOVE_PHYSICALLY;
            case MISSING -> item.isMutationWanted()
                    ? FeltStocktakeResolutionType.REMOVE_MISSING
                    : FeltStocktakeResolutionType.IGNORE_MISSING;
            case NOT_IN_STOCKTAKE, UNKNOWN -> FeltStocktakeResolutionType.IGNORE_MISSING;
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

    private FeltStocktakeResolutionDto toResolutionDto(FeltStocktakeItem item,
            FeltStocktakeResolutionType resolutionType,
            Storage expectedStorage, boolean stocktakeCompleted) {
        if (resolutionType == null) {
            return null;
        }
        boolean mutationOutsideStocktake =
                !stocktakeCompleted && hasMutationOutsideStocktake(item, resolutionType, expectedStorage);
        boolean mutationApplied = stocktakeCompleted
                ? item.isMutationApplied()
                : willApplyMutation(resolutionType, mutationOutsideStocktake);

        Storage newStorage = null;
        if (resolutionType == FeltStocktakeResolutionType.ADJUST_STORAGE) {
            newStorage = item.getNewStorage();
        } else if (resolutionType == FeltStocktakeResolutionType.MOVE_PHYSICALLY) {
            newStorage = expectedStorage;
        }

        return new FeltStocktakeResolutionDto(
                resolutionType,
                mutationOutsideStocktake,
                mutationApplied,
                newStorage != null ? newStorage.getId() : null,
                newStorage != null ? newStorage.getName() : null,
                item.getResolutionComment()
        );
    }

    private boolean willApplyMutation(FeltStocktakeResolutionType resolutionType, boolean mutationOutsideStocktake) {
        if (mutationOutsideStocktake) {
            return false;
        }
        return resolutionType == FeltStocktakeResolutionType.ADJUST_STORAGE
                || resolutionType == FeltStocktakeResolutionType.REMOVE_MISSING;
    }

    private boolean hasMutationOutsideStocktake(FeltStocktakeItem item, FeltStocktakeResolutionType resolutionType,
            Storage expectedStorage) {
        FeltStocktakeRollOrScrap rollOrScrap = item.getRollOrScrap();
        if (rollOrScrap == null) {
            return true;
        }
        Storage currentStorage = currentStorage(rollOrScrap);
        if (currentStorage == null) {
            return true;
        }

        return switch (resolutionType) {
            case ADJUST_STORAGE, MOVE_PHYSICALLY -> Objects.equals(currentStorage.getId(),
                    expectedStorage != null ? expectedStorage.getId() : null);
            case REMOVE_MISSING, IGNORE_MISSING -> expectedStorage == null
                    || !Objects.equals(currentStorage.getId(), expectedStorage.getId());
            case ACKNOWLEDGE -> false;
        };
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

    private Storage currentStorage(FeltStocktakeRollOrScrap rollOrScrap) {
        FeltRoll roll = rollOrScrap.getRoll();
        if (roll != null) {
            return roll.getStorage();
        }
        ScrapPiece scrap = rollOrScrap.getScrap();
        return scrap != null ? scrap.getStorage() : null;
    }

    private boolean isExpectedStorage(FeltStocktakeScan scan, Storage expectedStorage) {
        if (expectedStorage == null) {
            return false;
        }
        return Objects.equals(scan.getScannedStorage()
                                  .getId(), expectedStorage.getId());
    }

}