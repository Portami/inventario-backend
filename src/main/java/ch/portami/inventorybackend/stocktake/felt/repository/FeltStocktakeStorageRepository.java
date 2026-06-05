package ch.portami.inventorybackend.stocktake.felt.repository;

import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeStorage;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeStorageId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeltStocktakeStorageRepository extends JpaRepository<FeltStocktakeStorage, FeltStocktakeStorageId> {

    List<FeltStocktakeStorage> findByStocktakeId(Long stocktakeId);

    Optional<FeltStocktakeStorage> findByStocktakeIdAndStorageId(Long stocktakeId, Long storageId);

}
