package ch.portami.inventorybackend.stocktake.felt.service;

import ch.portami.inventorybackend.felt.entity.Felt;
import ch.portami.inventorybackend.felt.entity.FeltRoll;
import ch.portami.inventorybackend.felt.entity.ScrapPiece;
import ch.portami.inventorybackend.felt.repository.FeltRollRepository;
import ch.portami.inventorybackend.felt.repository.ScrapPieceRepository;
import ch.portami.inventorybackend.stocktake.felt.domain.FeltStocktakeItemEvaluation;
import ch.portami.inventorybackend.stocktake.felt.domain.FeltStocktakeItemEvaluator;
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
import ch.portami.inventorybackend.stocktake.felt.mapper.FeltStocktakeMapper;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeItemRepository;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeRepository;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeRollOrScrapRepository;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeScanRepository;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeStorageRepository;
import ch.portami.inventorybackend.storage.entity.Storage;
import ch.portami.inventorybackend.storage.exception.InvalidStorageReferenceException;
import ch.portami.inventorybackend.storage.repository.StorageRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FeltStocktakeService {

    private final FeltStocktakeRepository stocktakeRepo;
    private final FeltStocktakeStorageRepository stocktakeStorageRepo;
    private final FeltStocktakeItemRepository itemRepo;
    private final FeltStocktakeRollOrScrapRepository rollOrScrapRepo;
    private final FeltStocktakeScanRepository scanRepo;
    private final StorageRepository storageRepo;
    private final FeltRollRepository rollRepo;
    private final ScrapPieceRepository scrapRepo;
    private final FeltStocktakeItemEvaluator evaluator;
    private final FeltStocktakeMapper stocktakeMapper;

    public FeltStocktakeService(FeltStocktakeRepository stocktakeRepo,
            FeltStocktakeStorageRepository stocktakeStorageRepo,
            FeltStocktakeItemRepository itemRepo,
            FeltStocktakeRollOrScrapRepository rollOrScrapRepo,
            FeltStocktakeScanRepository scanRepo,
            StorageRepository storageRepo,
            FeltRollRepository rollRepo,
            ScrapPieceRepository scrapRepo,
            FeltStocktakeItemEvaluator evaluator,
            FeltStocktakeMapper stocktakeMapper) {
        this.stocktakeRepo = stocktakeRepo;
        this.stocktakeStorageRepo = stocktakeStorageRepo;
        this.itemRepo = itemRepo;
        this.rollOrScrapRepo = rollOrScrapRepo;
        this.scanRepo = scanRepo;
        this.storageRepo = storageRepo;
        this.rollRepo = rollRepo;
        this.scrapRepo = scrapRepo;
        this.evaluator = evaluator;
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
        List<FeltStocktakeStorage> stocktakeStorage = stocktakeStorageRepo.findByStocktakeId(id);

        for (FeltStocktakeStorage storageLink : stocktakeStorage) {
            if (!storageLink.isClosed()) {
                throw new UnclosedFeltStocktakeStorageException(id, storageLink.getStocktake()
                                                                               .getId());
            }
        }

        Set<Long> stocktakeStorageIds = stocktakeStorage.stream()
                                                        .map(link -> link.getStorage()
                                                                         .getId())
                                                        .collect(HashSet::new, Set::add, Set::addAll);

        List<FeltStocktakeItem> unusedItems = new ArrayList<>();
        List<FeltStocktakeItem> usedItems = new ArrayList<>();

        for (FeltStocktakeItem item : items) {

            if (!isExpectedStoragePartOfStocktake(item, id)) {
                unusedItems.add(item);
                continue;
            }

            boolean expectedStorageClosed = isExpectedStorageClosed(item, id);
            List<FeltStocktakeScan> scans = scanRepo.findByStocktakeIdAndStocktakeItemId(id, item.getId());
            FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, scans, false, expectedStorageClosed,
                    stocktakeStorageIds);
            if (evaluation.needsResolution()) {
                throw new UnresolvedFeltStocktakeProblemException(id, item.getId(), evaluation.status());
            }

            usedItems.add(item);

        }

        for (FeltStocktakeItem item : usedItems) {
            applyCompletionMutation(item, id, stocktakeStorageIds);
        }

        itemRepo.deleteAll(unusedItems);
        scanRepo.deleteByStocktakeIdAndVoidedIsTrue(id);

        stocktake.setCompletedAt(Instant.now());
        return stocktakeMapper.toFeltStocktakeDto(stocktake);
    }

    private void createItemsForStorages(FeltStocktake stocktake, boolean includeScrap) {
        List<FeltRoll> rolls = rollRepo.findAll();
        rolls.forEach(roll -> createItemForRoll(stocktake, roll));

        if (includeScrap) {
            List<ScrapPiece> scraps = scrapRepo.findAll();
            scraps.forEach(scrap -> createItemForScrap(stocktake, scrap));
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

    private void applyCompletionMutation(FeltStocktakeItem item, Long stocktakeId, Set<Long> stocktakeStorageIds) {
        Storage expectedStorage = item.getRollOrScrap() != null ? item.getRollOrScrap()
                                                                      .getExpectedStorage() : null;
        List<FeltStocktakeScan> scans = scanRepo.findByStocktakeIdAndStocktakeItemId(stocktakeId, item.getId());
        FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, scans, false, true, stocktakeStorageIds);

        if (!evaluation.hasProblem()) {
            return;
        }

        switch (evaluation.resolutionType()) {
            case ADJUST_STORAGE -> applyAdjustStorage(item);
            case REMOVE_MISSING -> applyRemoveMissing(item);
            case MOVE_PHYSICALLY -> markWrongStorageScansCorrected(scans, expectedStorage);
            case IGNORE_MISSING, ACKNOWLEDGE -> { /* Do nothing */ }
        }
    }

    private void applyAdjustStorage(FeltStocktakeItem item) {

        Storage newStorage = item.getNewStorage();

        Objects.requireNonNull(newStorage, "New storage must be provided for ADJUST_STORAGE resolution");

        FeltStocktakeRollOrScrap rollOrScrap = item.getRollOrScrap();

        if (rollOrScrap == null) {
            // The item was removed during the stocktake, so we cannot adjust storage.
            return;
        }

        if (rollOrScrap.getRoll() != null) {
            rollOrScrap.getRoll()
                       .setStorage(newStorage);
        } else if (rollOrScrap.getScrap() != null) {
            rollOrScrap.getScrap()
                       .setStorage(newStorage);
        }

        item.setMutationApplied(true);

    }

    private void applyRemoveMissing(FeltStocktakeItem item) {
        FeltStocktakeRollOrScrap rollOrScrap = item.getRollOrScrap();
        if (rollOrScrap == null) {
            // The item was already removed during the stocktake.
            return;
        }

        if (rollOrScrap.getRoll() != null) {
            rollRepo.delete(rollOrScrap.getRoll());
        } else if (rollOrScrap.getScrap() != null) {
            scrapRepo.delete(rollOrScrap.getScrap());
        }

        item.setMutationApplied(true);

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

    private boolean isExpectedStoragePartOfStocktake(FeltStocktakeItem item, Long stocktakeId) {
        if (item.getRollOrScrap() == null || item.getRollOrScrap()
                                                 .getExpectedStorage() == null) {
            return false;
        }
        Long storageId = item.getRollOrScrap()
                             .getExpectedStorage()
                             .getId();
        return stocktakeStorageRepo.findByStocktakeIdAndStorageId(stocktakeId, storageId)
                                   .isPresent();
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

    private List<Storage> resolveStorages(List<Long> storageIds) {
        if (storageIds == null || storageIds.isEmpty()) {
            return storageRepo.findAll();
        }
        List<Storage> storages = storageRepo.findAllById(storageIds);
        if (storages.size() != storageIds.size()) {
            throw new InvalidStorageReferenceException(-1L);
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