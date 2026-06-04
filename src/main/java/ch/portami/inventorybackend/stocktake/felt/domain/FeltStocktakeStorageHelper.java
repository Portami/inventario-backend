package ch.portami.inventorybackend.stocktake.felt.domain;

import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeStorage;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeStorageRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class FeltStocktakeStorageHelper {

    private final FeltStocktakeStorageRepository feltStocktakeStorageRepository;

    public FeltStocktakeStorageHelper(FeltStocktakeStorageRepository feltStocktakeStorageRepository) {
        this.feltStocktakeStorageRepository = feltStocktakeStorageRepository;
    }

    public Map<Long, Boolean> getStorageStatesOfStocktake(Long stocktakeId) {
        List<FeltStocktakeStorage> stocktakeStorages = feltStocktakeStorageRepository.findByStocktakeId(stocktakeId);

        Map<Long, Boolean> storageStates = new HashMap<>();

        for (FeltStocktakeStorage stocktakeStorage : stocktakeStorages) {
            storageStates.put(stocktakeStorage.getStorage()
                                              .getId(), stocktakeStorage.isClosed());
        }

        return storageStates;
    }

}
