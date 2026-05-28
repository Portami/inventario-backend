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

    @Transactional
    public void closeStorage(Long stocktakeId, Long storageId) {
        checkForUncompletedStocktake(stocktakeId);

        FeltStocktakeStorage storageLink = getStocktakeStorage(stocktakeId, storageId);

        if (storageLink.isClosed()) {
            return;
        }

        storageLink.setClosed(true);

    }

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
