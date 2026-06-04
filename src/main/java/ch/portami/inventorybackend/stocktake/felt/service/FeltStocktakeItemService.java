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

    public FeltStocktakeItemDto getItem(Long stocktakeId, Long itemId) {
        FeltStocktake stocktake = getStocktake(stocktakeId);
        FeltStocktakeItem item = itemRepo.findByStocktakeIdAndId(stocktakeId, itemId)
                                         .orElseThrow(
                                                 () -> new FeltStocktakeItemNotFoundException(stocktakeId, itemId));
        Map<Long, Boolean> storageStates = storageHelper.getStorageStatesOfStocktake(stocktakeId);

        return itemMapper.toDto(item, stocktake.getCompletedAt() != null, storageStates);
    }

    @Transactional
    public FeltStocktakeItemDto resolveProblem(Long stocktakeId, Long itemId, ResolveFeltStocktakeProblemDto dto) {
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

        if (!isValidResolutionType(evaluation.status(), dto.resolution())) {
            throw new InvalidFeltStocktakeResolutionType(stocktakeId, itemId,
                    FeltStocktakeItemApiStatusMapper.toApiStatus(evaluation.status()),
                    dto.resolution());
        }

        applyResolution(item, dto.resolution(), dto.comment());

        // Do not reuse the previous evaluation, as the status probably changed after applying the resolution
        return itemMapper.toDto(item, false, storageStates);
    }

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