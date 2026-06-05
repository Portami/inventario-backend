package ch.portami.inventorybackend.stocktake.felt.domain;

import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeStorage;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeStorageRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Helper class to retrieve the states of storages in a stocktake.
 */
@Component
public class FeltStocktakeStorageHelper {

    private final FeltStocktakeStorageRepository feltStocktakeStorageRepository;

    public FeltStocktakeStorageHelper(FeltStocktakeStorageRepository feltStocktakeStorageRepository) {
        this.feltStocktakeStorageRepository = feltStocktakeStorageRepository;
    }

    /**
     * Retrieves the states of storages in a stocktake.
     *
     * @param stocktakeId the ID of the stocktake
     * @return a map where the key is the storage ID and the value is a boolean indicating whether the storage is closed
     */
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
