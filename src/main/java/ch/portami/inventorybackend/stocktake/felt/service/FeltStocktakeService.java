package ch.portami.inventorybackend.stocktake.felt.service;

import ch.portami.inventorybackend.felt.entity.Felt;
import ch.portami.inventorybackend.felt.entity.FeltRoll;
import ch.portami.inventorybackend.felt.entity.ScrapPiece;
import ch.portami.inventorybackend.felt.repository.FeltRollRepository;
import ch.portami.inventorybackend.felt.repository.ScrapPieceRepository;
import ch.portami.inventorybackend.stocktake.felt.domain.FeltStocktakeItemEvaluation;
import ch.portami.inventorybackend.stocktake.felt.domain.FeltStocktakeItemEvaluator;
import ch.portami.inventorybackend.stocktake.felt.domain.FeltStocktakeItemStatus;
import ch.portami.inventorybackend.stocktake.felt.domain.FeltStocktakeStorageHelper;
import ch.portami.inventorybackend.stocktake.felt.dto.stocktake.CreateFeltStocktakeDto;
import ch.portami.inventorybackend.stocktake.felt.dto.stocktake.ExtendStocktakeDto;
import ch.portami.inventorybackend.stocktake.felt.dto.stocktake.FeltStocktakeDto;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktake;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeItem;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeRollOrScrap;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeScan;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeStorage;
import ch.portami.inventorybackend.stocktake.felt.exception.FeltStocktakeCompletedException;
import ch.portami.inventorybackend.stocktake.felt.exception.FeltStocktakeNotFoundException;
import ch.portami.inventorybackend.stocktake.felt.exception.UnclosedFeltStocktakeStorageException;
import ch.portami.inventorybackend.stocktake.felt.exception.UnresolvedFeltStocktakeProblemException;
import ch.portami.inventorybackend.stocktake.felt.mapper.FeltStocktakeItemApiStatusMapper;
import ch.portami.inventorybackend.stocktake.felt.mapper.FeltStocktakeMapper;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeItemRepository;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeRepository;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeRollOrScrapRepository;
import ch.portami.inventorybackend.storage.entity.Storage;
import ch.portami.inventorybackend.storage.exception.InvalidStorageReferenceException;
import ch.portami.inventorybackend.storage.repository.StorageRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FeltStocktakeService {

    private final FeltStocktakeRepository stocktakeRepo;
    private final FeltStocktakeItemRepository itemRepo;
    private final FeltStocktakeRollOrScrapRepository rollOrScrapRepo;
    private final StorageRepository storageRepo;
    private final FeltRollRepository rollRepo;
    private final ScrapPieceRepository scrapRepo;
    private final FeltStocktakeItemEvaluator evaluator;
    private final FeltStocktakeStorageHelper storageHelper;
    private final FeltStocktakeMapper stocktakeMapper;

    public FeltStocktakeService(FeltStocktakeRepository stocktakeRepo,
            FeltStocktakeItemRepository itemRepo,
            FeltStocktakeRollOrScrapRepository rollOrScrapRepo,
            StorageRepository storageRepo,
            FeltRollRepository rollRepo,
            ScrapPieceRepository scrapRepo,
            FeltStocktakeItemEvaluator evaluator,
            FeltStocktakeStorageHelper storageHelper,
            FeltStocktakeMapper stocktakeMapper) {
        this.stocktakeRepo = stocktakeRepo;
        this.itemRepo = itemRepo;
        this.rollOrScrapRepo = rollOrScrapRepo;
        this.storageRepo = storageRepo;
        this.rollRepo = rollRepo;
        this.scrapRepo = scrapRepo;
        this.evaluator = evaluator;
        this.storageHelper = storageHelper;
        this.stocktakeMapper = stocktakeMapper;
    }

    @Transactional
    public FeltStocktakeDto createStocktake(CreateFeltStocktakeDto dto) {
        FeltStocktake stocktake = stocktakeRepo.save(new FeltStocktake(dto.description()));

        List<Storage> storages = resolveStorages(dto.storageIds());
        storages.stream()
                .map(storage -> new FeltStocktakeStorage(stocktake, storage))
                .forEach(stocktake::addStorage);

        createItemsForStorages(stocktake, dto.includeScrap());

        return stocktakeMapper.toFeltStocktakeDto(stocktake);
    }

    public FeltStocktakeDto getStocktake(Long id) {
        return stocktakeMapper.toFeltStocktakeDto(getStocktakeEntity(id));
    }

    public List<FeltStocktakeDto> getAllStocktakes() {
        return stocktakeRepo.findAll()
                            .stream()
                            .map(stocktakeMapper::toFeltStocktakeDto)
                            .toList();
    }

    @Transactional
    public void deleteStocktake(Long id) {
        stocktakeRepo.deleteById(id);
    }

    @Transactional
    public FeltStocktakeDto extendStocktake(Long id, ExtendStocktakeDto dto) {
        FeltStocktake stocktake = getStocktakeEntity(id);
        ensureNotCompleted(stocktake);

        List<Storage> storages = resolveStorages(dto.storageIds());
        Set<Long> existingStorageIds = stocktake.getStorages()
                                                .stream()
                                                .map(link -> link.getStorage()
                                                                 .getId())
                                                .collect(HashSet::new, Set::add, Set::addAll);

        Set<FeltStocktakeStorage> stocktakeStorages = stocktake.getStorages();

        for (Storage storage : storages) {
            if (!existingStorageIds.contains(storage.getId())) {
                stocktakeStorages.add(new FeltStocktakeStorage(stocktake, storage));
            }
        }

        return stocktakeMapper.toFeltStocktakeDto(stocktake);
    }

    @Transactional
    public FeltStocktakeDto completeStocktake(Long id) {
        FeltStocktake stocktake = getStocktakeEntity(id);
        ensureNotCompleted(stocktake);

        List<FeltStocktakeItem> items = itemRepo.findByStocktakeId(id);
        Map<Long, Boolean> storageStates = storageHelper.getStorageStatesOfStocktake(id);

        for (Map.Entry<Long, Boolean> entry : storageStates.entrySet()) {
            if (Boolean.FALSE.equals(entry.getValue())) {
                throw new UnclosedFeltStocktakeStorageException(id, entry.getKey());
            }
        }

        List<FeltStocktakeItem> outOfScopeItems = new ArrayList<>();
        Map<FeltStocktakeItem, FeltStocktakeItemEvaluation> evaluationsOfItemsInScope = new HashMap<>();

        for (FeltStocktakeItem item : items) {

            FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, false, storageStates);

            if (evaluation.status() == FeltStocktakeItemStatus.OUT_OF_SCOPE) {
                outOfScopeItems.add(item);
                continue;
            }

            if (evaluation.needsResolution()) {
                throw new UnresolvedFeltStocktakeProblemException(id, item.getId(),
                        FeltStocktakeItemApiStatusMapper.toApiStatus(evaluation.status()));
            }

            evaluationsOfItemsInScope.put(item, evaluation);

        }

        for (Map.Entry<FeltStocktakeItem, FeltStocktakeItemEvaluation> entry : evaluationsOfItemsInScope.entrySet()) {
            applyCompletionMutation(entry.getKey(), entry.getValue());
            removeVoidedScans(entry.getKey());
        }

        itemRepo.deleteAll(outOfScopeItems);

        stocktake.setCompletedAt(Instant.now());
        return stocktakeMapper.toFeltStocktakeDto(stocktake);
    }

    private void createItemsForStorages(FeltStocktake stocktake, boolean includeScrap) {

        List<FeltRoll> rolls = rollRepo.findAll();
        for (FeltRoll roll : rolls) {
            if (roll.getStorage() != null) {
                createItemForRoll(stocktake, roll);
            }
        }

        if (includeScrap) {

            List<ScrapPiece> scraps = scrapRepo.findAll();
            for (ScrapPiece scrap : scraps) {
                if (scrap.getStorage() != null) {
                    createItemForScrap(stocktake, scrap);
                }
            }

        }

    }

    private void createItemForRoll(FeltStocktake stocktake, FeltRoll roll) {
        FeltStocktakeItem item = new FeltStocktakeItem(stocktake);
        item = itemRepo.save(item);

        Felt felt = roll.getFelt();

        FeltStocktakeRollOrScrap rollOrScrap = new FeltStocktakeRollOrScrap(
                item,
                roll.getStorage(),
                roll.getLength(),
                roll.getWidth(),
                felt.getColor(),
                felt.getThickness(),
                felt.getDensity(),
                felt.getPrice(),
                felt.getArticleNumber(),
                felt.getFeltType()
                    .getName(),
                felt.getSupplier()
                    .getName(),
                roll
        );
        rollOrScrap = rollOrScrapRepo.save(rollOrScrap);
        item.setRollOrScrap(rollOrScrap);
    }

    private void createItemForScrap(FeltStocktake stocktake, ScrapPiece scrap) {
        FeltStocktakeItem item = new FeltStocktakeItem(stocktake);
        item = itemRepo.save(item);

        Felt felt = scrap.getFelt();

        FeltStocktakeRollOrScrap rollOrScrap = new FeltStocktakeRollOrScrap(
                item,
                scrap.getStorage(),
                scrap.getLength(),
                scrap.getWidth(),
                felt.getColor(),
                felt.getThickness(),
                felt.getDensity(),
                felt.getPrice(),
                felt.getArticleNumber(),
                felt.getFeltType()
                    .getName(),
                felt.getSupplier()
                    .getName(),
                scrap
        );
        rollOrScrap = rollOrScrapRepo.save(rollOrScrap);
        item.setRollOrScrap(rollOrScrap);
    }

    private void applyCompletionMutation(FeltStocktakeItem item, FeltStocktakeItemEvaluation evaluation) {
        Storage expectedStorage = item.getRollOrScrap() != null ? item.getRollOrScrap()
                                                                      .getExpectedStorage() : null;

        if (!evaluation.hasProblem()) {
            return;
        }

        switch (evaluation.resolutionType()) {
            case ADJUST_STORAGE -> applyAdjustStorage(item);
            case REMOVE_MISSING -> applyRemoveMissing(item);
            case MOVE_PHYSICALLY -> markWrongStorageScansCorrected(item.getScans(), expectedStorage);
            case IGNORE_MISSING, ACKNOWLEDGE -> { /* Do nothing */ }
        }
    }

    private void applyAdjustStorage(FeltStocktakeItem item) {

        Storage newStorage = item.getNewStorage();

        Objects.requireNonNull(newStorage, "New storage must be provided for ADJUST_STORAGE resolution");

        FeltStocktakeRollOrScrap rollOrScrap = item.getRollOrScrap();

        if (rollOrScrap == null) {
            throw new IllegalStateException("Cannot adjust storage of unknown item without roll or scrap");
        }

        if (rollOrScrap.getRoll() != null) {
            rollOrScrap.getRoll()
                       .setStorage(newStorage);
            item.setMutationApplied(true);
        } else if (rollOrScrap.getScrap() != null) {
            rollOrScrap.getScrap()
                       .setStorage(newStorage);
            item.setMutationApplied(true);
        }

    }

    private void applyRemoveMissing(FeltStocktakeItem item) {
        FeltStocktakeRollOrScrap rollOrScrap = item.getRollOrScrap();

        if (rollOrScrap == null) {
            throw new IllegalStateException("Cannot remove unknown item without roll or scrap");
        }

        if (rollOrScrap.getRoll() != null) {
            rollRepo.delete(rollOrScrap.getRoll());
            item.setMutationApplied(true);
        } else if (rollOrScrap.getScrap() != null) {
            scrapRepo.delete(rollOrScrap.getScrap());
            item.setMutationApplied(true);
        }

    }

    private void markWrongStorageScansCorrected(List<FeltStocktakeScan> scans, Storage expectedStorage) {

        for (FeltStocktakeScan scan : scans) {
            if (scan.isVoided()) {
                continue;
            }
            if (expectedStorage == null || !scan.getScannedStorage()
                                                .getId()
                                                .equals(expectedStorage.getId())) {
                scan.setCorrected(true);
            }
        }

    }

    private void removeVoidedScans(FeltStocktakeItem item) {
        for (FeltStocktakeScan scan : item.getScans()) {
            if (scan.isVoided()) {
                item.removeScan(scan);
            }
        }
    }

    private List<Storage> resolveStorages(List<Long> storageIds) {
        if (storageIds == null) {
            return storageRepo.findAll();
        }

        List<Storage> storages = storageRepo.findAllById(storageIds);

        if (storages.size() != storageIds.size()) {
            for (Long storageId : storageIds) {
                if (storages.stream()
                            .noneMatch(storage -> storage.getId()
                                                         .equals(storageId))) {
                    throw new InvalidStorageReferenceException(storageId);
                }
            }
        }

        return storages;
    }

    private FeltStocktake getStocktakeEntity(Long id) {
        return stocktakeRepo.findById(id)
                            .orElseThrow(() -> new FeltStocktakeNotFoundException(id));
    }

    private void ensureNotCompleted(FeltStocktake stocktake) {
        if (stocktake.getCompletedAt() != null) {
            throw new FeltStocktakeCompletedException(stocktake.getId());
        }
    }
}