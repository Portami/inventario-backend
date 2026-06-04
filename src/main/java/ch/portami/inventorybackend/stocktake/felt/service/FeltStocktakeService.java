package ch.portami.inventorybackend.stocktake.felt.service;

import ch.portami.inventorybackend.felt.entity.FeltRoll;
import ch.portami.inventorybackend.felt.entity.ScrapPiece;
import ch.portami.inventorybackend.felt.repository.FeltRollRepository;
import ch.portami.inventorybackend.felt.repository.ScrapPieceRepository;
import ch.portami.inventorybackend.stocktake.felt.domain.FeltStocktakeItemEvaluation;
import ch.portami.inventorybackend.stocktake.felt.domain.FeltStocktakeItemEvaluator;
import ch.portami.inventorybackend.stocktake.felt.domain.FeltStocktakeItemStatus;
import ch.portami.inventorybackend.stocktake.felt.domain.FeltStocktakeRollOrScrapHelper;
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

/**
 * Service that manages felt stocktakes, including creation, retrieval, extension, completion, and deletion of
 * stocktakes.
 */
@Service
@Transactional(readOnly = true)
public class FeltStocktakeService {

    private final FeltStocktakeRepository stocktakeRepo;
    private final FeltStocktakeItemRepository itemRepo;
    private final StorageRepository storageRepo;
    private final FeltRollRepository rollRepo;
    private final ScrapPieceRepository scrapRepo;
    private final FeltStocktakeItemEvaluator evaluator;
    private final FeltStocktakeStorageHelper storageHelper;
    private final FeltStocktakeRollOrScrapHelper rollOrScrapHelper;
    private final FeltStocktakeMapper stocktakeMapper;

    public FeltStocktakeService(FeltStocktakeRepository stocktakeRepo,
            FeltStocktakeItemRepository itemRepo,
            StorageRepository storageRepo,
            FeltRollRepository rollRepo,
            ScrapPieceRepository scrapRepo,
            FeltStocktakeItemEvaluator evaluator,
            FeltStocktakeStorageHelper storageHelper,
            FeltStocktakeRollOrScrapHelper rollOrScrapHelper,
            FeltStocktakeMapper stocktakeMapper) {
        this.stocktakeRepo = stocktakeRepo;
        this.itemRepo = itemRepo;
        this.storageRepo = storageRepo;
        this.rollRepo = rollRepo;
        this.scrapRepo = scrapRepo;
        this.evaluator = evaluator;
        this.storageHelper = storageHelper;
        this.rollOrScrapHelper = rollOrScrapHelper;
        this.stocktakeMapper = stocktakeMapper;
    }

    /**
     * Creates a new felt stocktake with the given description and initial scope.
     *
     * @param createDto the DTO containing the description and initial scope of the stocktake
     * @return the created stocktake as a DTO
     * @throws InvalidStorageReferenceException if any of the provided storage IDs do not correspond to existing
     *                                          storages
     */
    @Transactional
    public FeltStocktakeDto createStocktake(CreateFeltStocktakeDto createDto) {
        FeltStocktake stocktake = stocktakeRepo.save(new FeltStocktake(createDto.description()));

        List<Storage> storages = resolveStorages(createDto.storageIds());
        storages.stream()
                .map(storage -> new FeltStocktakeStorage(stocktake, storage))
                .forEach(stocktake::addStorage);

        createItemsForStorages(stocktake, createDto.includeScrap());

        return stocktakeMapper.toFeltStocktakeDto(stocktake);
    }

    /**
     * Retrieves a felt stocktake by its ID.
     *
     * @param id the ID of the stocktake to retrieve
     * @return the retrieved stocktake as a DTO
     * @throws FeltStocktakeNotFoundException if no stocktake with the given ID exists
     */
    public FeltStocktakeDto getStocktake(Long id) {
        return stocktakeMapper.toFeltStocktakeDto(getStocktakeEntity(id));
    }

    /**
     * Retrieves all felt stocktakes.
     *
     * @return a list of all stocktakes as DTOs
     */
    public List<FeltStocktakeDto> getAllStocktakes() {
        return stocktakeRepo.findAll()
                            .stream()
                            .map(stocktakeMapper::toFeltStocktakeDto)
                            .toList();
    }

    /**
     * Deletes a felt stocktake by its ID. If the stocktake does not exist, no action is taken.
     *
     * @param id the ID of the stocktake to delete.
     */
    @Transactional
    public void deleteStocktake(Long id) {
        stocktakeRepo.deleteById(id);
    }

    /**
     * Extends the scope of an existing felt stocktake by adding new storages. The stocktake must not be completed.
     *
     * @param id  the ID of the stocktake to extend
     * @param dto the DTO containing the IDs of the storages to add to the stocktake. If null, all existing storages
     *            will be added.
     * @return the updated stocktake as a DTO
     * @throws FeltStocktakeNotFoundException   if no stocktake with the given ID exists
     * @throws FeltStocktakeCompletedException  if the stocktake has already been completed
     * @throws InvalidStorageReferenceException if any of the provided storage IDs do not correspond to existing
     *                                          storages
     */
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

    /**
     * Completes a felt stocktake by evaluating all items and applying necessary mutations based the chosen
     * resolutions.
     *
     * @param id the ID of the stocktake to complete
     * @return the completed stocktake as a DTO
     * @throws FeltStocktakeNotFoundException          if no stocktake with the given ID exists
     * @throws FeltStocktakeCompletedException         if the stocktake has already been completed
     * @throws UnclosedFeltStocktakeStorageException   if any storage in the stocktake is not closed
     * @throws UnresolvedFeltStocktakeProblemException if any item in the stocktake has a problem that requires
     *                                                 resolution but has not been resolved yet
     */
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
            Storage rollStorage = roll.getStorage();
            if (rollStorage != null) {
                rollOrScrapHelper.createAndSaveItemForRoll(stocktake, roll, rollStorage);
            }
        }

        if (includeScrap) {

            List<ScrapPiece> scraps = scrapRepo.findAll();
            for (ScrapPiece scrap : scraps) {
                Storage scrapStorage = scrap.getStorage();
                if (scrapStorage != null) {
                    rollOrScrapHelper.createAndSaveItemForScrap(stocktake, scrap, scrapStorage);
                }
            }

        }

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