package ch.portami.inventorybackend.stocktake.felt.service;

import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktake;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeStorage;
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
        FeltStocktake stocktake = getStocktake(stocktakeId);

        if (stocktake.getCompletedAt() != null) {
            return;
        }

        FeltStocktakeStorage storageLink = getStocktakeStorage(stocktakeId, storageId);
        storageLink.setClosed(true);

    }

    @Transactional
    public void reopenStorage(Long stocktakeId, Long storageId) {
        FeltStocktake stocktake = getStocktake(stocktakeId);

        if (stocktake.getCompletedAt() == null) {
            return;
        }

        FeltStocktakeStorage storageLink = getStocktakeStorage(stocktakeId, storageId);
        storageLink.setClosed(false);

    }

    private FeltStocktake getStocktake(Long stocktakeId) {
        return stocktakeRepo.findById(stocktakeId)
                            .orElseThrow(() -> new FeltStocktakeNotFoundException(stocktakeId));
    }

    private FeltStocktakeStorage getStocktakeStorage(Long stocktakeId, Long storageId) {
        return stocktakeStorageRepo.findByStocktakeIdAndStorageId(stocktakeId, storageId)
                                   .orElseThrow(
                                           () -> new FeltStocktakeStorageNotFoundException(stocktakeId, storageId));
    }

}
