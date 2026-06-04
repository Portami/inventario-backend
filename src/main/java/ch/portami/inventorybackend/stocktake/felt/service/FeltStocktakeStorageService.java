package ch.portami.inventorybackend.stocktake.felt.service;

import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktake;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeStorage;
import ch.portami.inventorybackend.stocktake.felt.exception.FeltStocktakeCompletedException;
import ch.portami.inventorybackend.stocktake.felt.exception.FeltStocktakeNotFoundException;
import ch.portami.inventorybackend.stocktake.felt.exception.FeltStocktakeStorageNotFoundException;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeRepository;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeStorageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing the state of storages in a felt stocktake.
 */
@Service
@Transactional(readOnly = true)
public class FeltStocktakeStorageService {

    private final FeltStocktakeRepository stocktakeRepo;
    private final FeltStocktakeStorageRepository stocktakeStorageRepo;

    public FeltStocktakeStorageService(FeltStocktakeRepository stocktakeRepo,
            FeltStocktakeStorageRepository stocktakeStorageRepo) {
        this.stocktakeRepo = stocktakeRepo;
        this.stocktakeStorageRepo = stocktakeStorageRepo;
    }

    /**
     * Closes a storage in a stocktake, marking items expected in this storage as missing. Does nothing if the storage
     * is already closed.
     *
     * @param stocktakeId the ID of the stocktake
     * @param storageId   the ID of the storage to close
     * @throws FeltStocktakeNotFoundException        if the stocktake with the given ID does not exist
     * @throws FeltStocktakeCompletedException       if the stocktake is already completed
     * @throws FeltStocktakeStorageNotFoundException if the storage with the given ID is not part of the stocktake
     */
    @Transactional
    public void closeStorage(Long stocktakeId, Long storageId) {
        checkForUncompletedStocktake(stocktakeId);

        FeltStocktakeStorage storageLink = getStocktakeStorage(stocktakeId, storageId);

        if (storageLink.isClosed()) {
            return;
        }

        storageLink.setClosed(true);

    }

    /**
     * Reopens a storage in a stocktake. Does nothing if the storage is still open.
     *
     * @param stocktakeId the ID of the stocktake
     * @param storageId   the ID of the storage to reopen
     * @throws FeltStocktakeNotFoundException        if the stocktake with the given ID does not exist
     * @throws FeltStocktakeCompletedException       if the stocktake is already completed
     * @throws FeltStocktakeStorageNotFoundException if the storage with the given ID is not part of the stocktake
     */
    @Transactional
    public void reopenStorage(Long stocktakeId, Long storageId) {
        checkForUncompletedStocktake(stocktakeId);

        FeltStocktakeStorage storageLink = getStocktakeStorage(stocktakeId, storageId);

        if (!storageLink.isClosed()) {
            return;
        }

        storageLink.setClosed(false);

    }

    private void checkForUncompletedStocktake(Long stocktakeId) {
        FeltStocktake stocktake = stocktakeRepo.findById(stocktakeId)
                                               .orElseThrow(() -> new FeltStocktakeNotFoundException(stocktakeId));

        if (stocktake.getCompletedAt() != null) {
            throw new FeltStocktakeCompletedException(stocktakeId);
        }

    }

    private FeltStocktakeStorage getStocktakeStorage(Long stocktakeId, Long storageId) {
        return stocktakeStorageRepo.findByStocktakeIdAndStorageId(stocktakeId, storageId)
                                   .orElseThrow(
                                           () -> new FeltStocktakeStorageNotFoundException(stocktakeId, storageId));
    }

}
