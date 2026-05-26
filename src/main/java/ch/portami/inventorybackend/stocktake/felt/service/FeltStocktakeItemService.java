package ch.portami.inventorybackend.stocktake.felt.service;

import ch.portami.inventorybackend.core.storage.entity.Storage;
import ch.portami.inventorybackend.stocktake.felt.domain.FeltStocktakeItemEvaluation;
import ch.portami.inventorybackend.stocktake.felt.domain.FeltStocktakeItemEvaluator;
import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeItemDto;
import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeItemStatus;
import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeResolutionType;
import ch.portami.inventorybackend.stocktake.felt.dto.item.ResolveFeltStocktakeProblemDto;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktake;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeItem;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeRollOrScrap;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeScan;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeStorage;
import ch.portami.inventorybackend.stocktake.felt.exception.FeltStocktakeCompletedException;
import ch.portami.inventorybackend.stocktake.felt.exception.FeltStocktakeItemNotFoundException;
import ch.portami.inventorybackend.stocktake.felt.exception.FeltStocktakeNotFoundException;
import ch.portami.inventorybackend.stocktake.felt.exception.InvalidFeltStocktakeResolutionType;
import ch.portami.inventorybackend.stocktake.felt.mapper.FeltStocktakeItemMapper;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeItemRepository;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeRepository;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeScanRepository;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeStorageRepository;
import jakarta.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FeltStocktakeItemService {

    private final FeltStocktakeRepository stocktakeRepo;
    private final FeltStocktakeItemRepository itemRepo;
    private final FeltStocktakeScanRepository scanRepo;
    private final FeltStocktakeStorageRepository stocktakeStorageRepo;
    private final FeltStocktakeItemEvaluator evaluator;
    private final FeltStocktakeItemMapper itemMapper;

    public FeltStocktakeItemService(FeltStocktakeRepository stocktakeRepo,
            FeltStocktakeItemRepository itemRepo,
            FeltStocktakeScanRepository scanRepo,
            FeltStocktakeStorageRepository stocktakeStorageRepo,
            FeltStocktakeItemEvaluator evaluator,
            FeltStocktakeItemMapper itemMapper) {
        this.stocktakeRepo = stocktakeRepo;
        this.itemRepo = itemRepo;
        this.scanRepo = scanRepo;
        this.stocktakeStorageRepo = stocktakeStorageRepo;
        this.evaluator = evaluator;
        this.itemMapper = itemMapper;
    }

    public List<FeltStocktakeItemDto> getItems(Long stocktakeId, @Nullable Long storageId) {
        FeltStocktake stocktake = getStocktake(stocktakeId);
        List<FeltStocktakeItem> items = itemRepo.findByStocktakeId(stocktakeId);
        Set<Long> stocktakeStorageIds = stocktakeStorageIds(stocktakeId);

        Stream<FeltStocktakeItem> itemStream = items.stream();

        if (storageId != null) {
            itemStream = itemStream.filter(item -> matchesStorage(item, storageId));
        } else {
            itemStream = itemStream.filter(item -> matchesAnyStorage(item, stocktakeStorageIds));
        }

        return itemStream.map(item -> {
                             boolean expectedStorageClosed = isExpectedStorageClosed(item, stocktakeId);
                             return itemMapper.toDto(item, item.getScans(), stocktake.getCompletedAt() != null, expectedStorageClosed,
                                     stocktakeStorageIds);
                         })
                         .toList();
    }

    public FeltStocktakeItemDto getItem(Long stocktakeId, Long itemId) {
        FeltStocktake stocktake = getStocktake(stocktakeId);
        FeltStocktakeItem item = itemRepo.findByStocktakeIdAndId(stocktakeId, itemId)
                                         .orElseThrow(
                                                 () -> new FeltStocktakeItemNotFoundException(stocktakeId, itemId));
        List<FeltStocktakeScan> scans = scanRepo.findByStocktakeIdAndStocktakeItemId(stocktakeId, itemId);
        boolean expectedStorageClosed = isExpectedStorageClosed(item, stocktakeId);
        return itemMapper.toDto(item, scans, stocktake.getCompletedAt() != null, expectedStorageClosed,
                stocktakeStorageIds(stocktakeId));
    }

    @Transactional
    public FeltStocktakeItemDto resolveProblem(Long stocktakeId, Long itemId, ResolveFeltStocktakeProblemDto dto) {
        FeltStocktake stocktake = getStocktake(stocktakeId);
        ensureNotCompleted(stocktake);

        FeltStocktakeItem item = itemRepo.findByStocktakeIdAndId(stocktakeId, itemId)
                                         .orElseThrow(
                                                 () -> new FeltStocktakeItemNotFoundException(stocktakeId, itemId));
        List<FeltStocktakeScan> scans = scanRepo.findByStocktakeIdAndStocktakeItemId(stocktakeId, itemId);
        boolean expectedStorageClosed = isExpectedStorageClosed(item, stocktakeId);
        Set<Long> stocktakeStorageIds = stocktakeStorageIds(stocktakeId);

        FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, scans, false, expectedStorageClosed,
                stocktakeStorageIds);

        if (!isValidResolutionType(evaluation.status(), dto.resolution())) {
            throw new InvalidFeltStocktakeResolutionType(stocktakeId, itemId, evaluation.status(),
                    evaluation.resolutionType());
        }

        applyResolution(item, dto.resolution(), dto.comment(), scans);

        return itemMapper.toDto(item, scans, false, expectedStorageClosed, stocktakeStorageIds);
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

        List<FeltStocktakeScan> scans = scanRepo.findByStocktakeIdAndStocktakeItemId(stocktakeId, itemId);
        boolean expectedStorageClosed = isExpectedStorageClosed(item, stocktakeId);
        return itemMapper.toDto(item, scans, false, expectedStorageClosed, stocktakeStorageIds(stocktakeId));
    }

    private boolean isValidResolutionType(FeltStocktakeItemStatus status, FeltStocktakeResolutionType resolutionType) {
        return switch (resolutionType) {
            case ADJUST_STORAGE, MOVE_PHYSICALLY -> status == FeltStocktakeItemStatus.WRONG_STORAGE;
            case REMOVE_MISSING, IGNORE_MISSING -> status == FeltStocktakeItemStatus.MISSING;
            case ACKNOWLEDGE ->
                    status == FeltStocktakeItemStatus.UNKNOWN || status == FeltStocktakeItemStatus.NOT_IN_STOCKTAKE;
        };
    }

    private void applyResolution(FeltStocktakeItem item, FeltStocktakeResolutionType resolution, String comment,
            List<FeltStocktakeScan> scans) {

        item.setProblemAcknowledged(true);
        item.setResolutionComment(comment);

        if (resolution == FeltStocktakeResolutionType.ADJUST_STORAGE) {
            item.setMutationWanted(true);
            item.setNewStorage(resolveScannedStorage(scans));
        } else if (resolution == FeltStocktakeResolutionType.REMOVE_MISSING) {
            item.setMutationWanted(true);
        }

    }

    private Storage resolveScannedStorage(
            List<FeltStocktakeScan> scans) {
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

    private boolean matchesAnyStorage(FeltStocktakeItem item, Set<Long> stocktakeStorageIds) {
        FeltStocktakeRollOrScrap rollOrScrap = item.getRollOrScrap();

        if (rollOrScrap != null) {

            Storage expectedStorage = rollOrScrap.getExpectedStorage();

            if (expectedStorage != null && stocktakeStorageIds.contains(expectedStorage.getId())) {
                return true;
            }

        }

        return item.getScans()
                   .stream()
                   .filter(scan -> !scan.isVoided())
                   .anyMatch(scan -> stocktakeStorageIds.contains(scan.getScannedStorage()
                                                                      .getId()));

    }

    private boolean isExpectedStorageClosed(FeltStocktakeItem item, Long stocktakeId) {
        if (item.getRollOrScrap() == null || item.getRollOrScrap()
                                                 .getExpectedStorage() == null) {
            return false;
        }
        Long storageId = item.getRollOrScrap()
                             .getExpectedStorage()
                             .getId();
        return stocktakeStorageRepo.findByStocktakeIdAndStorageId(stocktakeId, storageId)
                                   .map(FeltStocktakeStorage::isClosed)
                                   .orElse(false);
    }

    private Set<Long> stocktakeStorageIds(Long stocktakeId) {
        return stocktakeStorageRepo.findByStocktakeId(stocktakeId)
                                   .stream()
                                   .map(link -> link.getStorage()
                                                    .getId())
                                   .collect(HashSet::new, Set::add, Set::addAll);
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