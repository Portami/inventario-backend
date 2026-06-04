package ch.portami.inventorybackend.stocktake.felt.service;

import ch.portami.inventorybackend.stocktake.felt.domain.FeltStocktakeItemEvaluation;
import ch.portami.inventorybackend.stocktake.felt.domain.FeltStocktakeItemEvaluator;
import ch.portami.inventorybackend.stocktake.felt.domain.FeltStocktakeItemStatus;
import ch.portami.inventorybackend.stocktake.felt.domain.FeltStocktakeResolutionType;
import ch.portami.inventorybackend.stocktake.felt.domain.FeltStocktakeStorageHelper;
import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeItemDto;
import ch.portami.inventorybackend.stocktake.felt.dto.item.ResolveFeltStocktakeProblemDto;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktake;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeItem;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeRollOrScrap;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeScan;
import ch.portami.inventorybackend.stocktake.felt.exception.FeltStocktakeCompletedException;
import ch.portami.inventorybackend.stocktake.felt.exception.FeltStocktakeItemNotFoundException;
import ch.portami.inventorybackend.stocktake.felt.exception.FeltStocktakeNotFoundException;
import ch.portami.inventorybackend.stocktake.felt.exception.InvalidFeltStocktakeResolutionType;
import ch.portami.inventorybackend.stocktake.felt.exception.NoFeltStocktakeProblemToResolveException;
import ch.portami.inventorybackend.stocktake.felt.mapper.FeltStocktakeItemApiStatusMapper;
import ch.portami.inventorybackend.stocktake.felt.mapper.FeltStocktakeItemMapper;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeItemRepository;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeRepository;
import ch.portami.inventorybackend.storage.entity.Storage;
import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing felt stocktake items, including retrieving item details and resolving problems with items during
 * a stocktake.
 */
@Service
@Transactional(readOnly = true)
public class FeltStocktakeItemService {

    private final FeltStocktakeRepository stocktakeRepo;
    private final FeltStocktakeItemRepository itemRepo;
    private final FeltStocktakeItemEvaluator evaluator;
    private final FeltStocktakeStorageHelper storageHelper;
    private final FeltStocktakeItemMapper itemMapper;

    public FeltStocktakeItemService(FeltStocktakeRepository stocktakeRepo,
            FeltStocktakeItemRepository itemRepo,
            FeltStocktakeItemEvaluator evaluator,
            FeltStocktakeStorageHelper storageHelper,
            FeltStocktakeItemMapper itemMapper) {
        this.stocktakeRepo = stocktakeRepo;
        this.itemRepo = itemRepo;
        this.evaluator = evaluator;
        this.storageHelper = storageHelper;
        this.itemMapper = itemMapper;
    }

    /**
     * Retrieves the items of a stocktake, optionally filtered by storage. If a storage ID is provided, only items that
     * are expected to be in that storage or have been scanned in that storage will be returned. If no storage ID is
     * provided, all items of this stocktake will be returned.
     *
     * @param stocktakeId the ID of the stocktake for which to retrieve the items
     * @param storageId   the optional ID of the storage to filter items by
     * @return a list of items matching the criteria, each with its evaluated status and resolution information if
     * applicable
     * @throws FeltStocktakeNotFoundException if the stocktake with the given ID does not exist
     */
    public List<FeltStocktakeItemDto> getItems(Long stocktakeId, @Nullable Long storageId) {

        FeltStocktake stocktake = getStocktake(stocktakeId);
        List<FeltStocktakeItem> items = itemRepo.findByStocktakeId(stocktakeId);
        Map<Long, Boolean> storageStates = storageHelper.getStorageStatesOfStocktake(stocktakeId);
        boolean stocktakeCompleted = stocktake.getCompletedAt() != null;

        List<FeltStocktakeItemDto> itemsToReturn = new ArrayList<>();

        if (storageId == null) {

            for (FeltStocktakeItem item : items) {
                FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, stocktakeCompleted, storageStates);

                if (evaluation.status() != FeltStocktakeItemStatus.OUT_OF_SCOPE) {
                    itemsToReturn.add(itemMapper.toDto(item, evaluation));
                }
            }

        } else {

            for (FeltStocktakeItem item : items) {
                if (matchesStorage(item, storageId)) {
                    itemsToReturn.add(itemMapper.toDto(item, stocktakeCompleted, storageStates));
                }
            }
        }

        return itemsToReturn;
    }

    /**
     * Retrieves the details of a specific stocktake item, including its evaluated status and resolution information if
     * applicable.
     *
     * @param stocktakeId the ID of the stocktake to which the item belongs
     * @param itemId      the ID of the item to retrieve
     * @return the details of the item, including its evaluated status and resolution information if applicable
     * @throws FeltStocktakeNotFoundException     if the stocktake with the given ID does not exist
     * @throws FeltStocktakeItemNotFoundException if the item with the given ID does not exist in the specified
     *                                            stocktake
     */
    public FeltStocktakeItemDto getItem(Long stocktakeId, Long itemId) {
        FeltStocktake stocktake = getStocktake(stocktakeId);
        FeltStocktakeItem item = itemRepo.findByStocktakeIdAndId(stocktakeId, itemId)
                                         .orElseThrow(
                                                 () -> new FeltStocktakeItemNotFoundException(stocktakeId, itemId));
        Map<Long, Boolean> storageStates = storageHelper.getStorageStatesOfStocktake(stocktakeId);

        return itemMapper.toDto(item, stocktake.getCompletedAt() != null, storageStates);
    }

    /**
     * Resolves a problem of a stocktake item by applying the specified resolution. The type of resolution must be valid
     * for the item's problem status.
     *
     * @param stocktakeId   the ID of the stocktake to which the item belongs
     * @param itemId        the ID of the item for which to resolve the problem
     * @param resolutionDto the DTO containing the resolution type
     * @return the details of the item after applying the resolution, including its new evaluated status and resolution
     * @throws FeltStocktakeNotFoundException           if the stocktake with the given ID does not exist
     * @throws FeltStocktakeItemNotFoundException       if the item with the given ID does not exist in the specified
     *                                                  stocktake
     * @throws NoFeltStocktakeProblemToResolveException if the item does not have a problem that needs to be resolved
     * @throws InvalidFeltStocktakeResolutionType       if the provided resolution type is not valid for the item's
     *                                                  problem status
     * @throws FeltStocktakeCompletedException          if the stocktake has already been completed
     */
    @Transactional
    public FeltStocktakeItemDto resolveProblem(Long stocktakeId, Long itemId,
            ResolveFeltStocktakeProblemDto resolutionDto) {
        FeltStocktake stocktake = getStocktake(stocktakeId);
        ensureNotCompleted(stocktake);

        FeltStocktakeItem item = itemRepo.findByStocktakeIdAndId(stocktakeId, itemId)
                                         .orElseThrow(
                                                 () -> new FeltStocktakeItemNotFoundException(stocktakeId, itemId));

        Map<Long, Boolean> storageStates = storageHelper.getStorageStatesOfStocktake(stocktakeId);

        FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, false, storageStates);

        if (!evaluation.needsResolution()) {
            throw new NoFeltStocktakeProblemToResolveException(stocktakeId, itemId);
        }

        if (!isValidResolutionType(evaluation.status(), resolutionDto.resolution())) {
            throw new InvalidFeltStocktakeResolutionType(stocktakeId, itemId,
                    FeltStocktakeItemApiStatusMapper.toApiStatus(evaluation.status()),
                    resolutionDto.resolution());
        }

        applyResolution(item, resolutionDto.resolution(), resolutionDto.comment());

        // Do not reuse the previous evaluation, as the status probably changed after applying the resolution
        return itemMapper.toDto(item, false, storageStates);
    }

    /**
     * Unresolves a previously resolved problem of a stocktake item, resetting all resolution-related fields of the
     * item.
     *
     * @param stocktakeId the ID of the stocktake to which the item belongs
     * @param itemId      the ID of the item for which to unresolve the problem
     * @return the details of the item after unresolving the problem, including its new evaluated status
     * @throws FeltStocktakeNotFoundException     if the stocktake with the given ID does not exist
     * @throws FeltStocktakeItemNotFoundException if the item with the given ID does not exist in the specified
     *                                            stocktake
     * @throws FeltStocktakeCompletedException    if the stocktake has already been completed
     */
    @Transactional
    public FeltStocktakeItemDto unresolveProblem(Long stocktakeId, Long itemId) {
        FeltStocktake stocktake = getStocktake(stocktakeId);
        ensureNotCompleted(stocktake);

        FeltStocktakeItem item = itemRepo.findByStocktakeIdAndId(stocktakeId, itemId)
                                         .orElseThrow(
                                                 () -> new FeltStocktakeItemNotFoundException(stocktakeId, itemId));

        item.setProblemAcknowledged(false);
        item.setMutationWanted(false);
        item.setNewStorage(null);
        item.setMutationApplied(false);
        item.setResolutionComment(null);

        Map<Long, Boolean> storageStates = storageHelper.getStorageStatesOfStocktake(stocktakeId);
        return itemMapper.toDto(item, false, storageStates);
    }

    private boolean isValidResolutionType(FeltStocktakeItemStatus status,
            FeltStocktakeResolutionType resolutionType) {
        return switch (resolutionType) {
            case ADJUST_STORAGE, MOVE_PHYSICALLY -> status == FeltStocktakeItemStatus.WRONG_STORAGE;
            case REMOVE_MISSING, IGNORE_MISSING -> status == FeltStocktakeItemStatus.MISSING;
            case ACKNOWLEDGE -> status == FeltStocktakeItemStatus.UNKNOWN
                    || status == FeltStocktakeItemStatus.NOT_IN_STOCKTAKE;
        };
    }

    private void applyResolution(FeltStocktakeItem item, FeltStocktakeResolutionType resolution, String comment) {

        item.setProblemAcknowledged(true);
        item.setResolutionComment(comment);

        if (resolution == FeltStocktakeResolutionType.ADJUST_STORAGE) {
            item.setMutationWanted(true);
            item.setNewStorage(resolveScannedStorage(item.getScans()));
        } else if (resolution == FeltStocktakeResolutionType.REMOVE_MISSING) {
            item.setMutationWanted(true);
        }

    }

    private Storage resolveScannedStorage(List<FeltStocktakeScan> scans) {
        return scans.stream()
                    .filter(scan -> !scan.isVoided())
                    .filter(scan -> !scan.isCorrected())
                    .findFirst()
                    .map(FeltStocktakeScan::getScannedStorage)
                    .orElseThrow();
    }

    private boolean matchesStorage(FeltStocktakeItem item, Long storageId) {

        FeltStocktakeRollOrScrap rollOrScrap = item.getRollOrScrap();

        if (rollOrScrap != null) {

            Storage expectedStorage = rollOrScrap.getExpectedStorage();

            if (expectedStorage != null && expectedStorage.getId()
                                                          .equals(storageId)) {
                return true;
            }

        }

        return item.getScans()
                   .stream()
                   .anyMatch(scan -> !scan.isVoided() && scan.getScannedStorage()
                                                             .getId()
                                                             .equals(storageId));

    }

    private FeltStocktake getStocktake(Long stocktakeId) {
        return stocktakeRepo.findById(stocktakeId)
                            .orElseThrow(() -> new FeltStocktakeNotFoundException(stocktakeId));
    }

    private void ensureNotCompleted(FeltStocktake stocktake) {
        if (stocktake.getCompletedAt() != null) {
            throw new FeltStocktakeCompletedException(stocktake.getId());
        }
    }

}