package ch.portami.inventorybackend.stocktake.felt.repository;

import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeScan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeltStocktakeScanRepository extends JpaRepository<FeltStocktakeScan, Long> {

    Optional<FeltStocktakeScan> findByStocktakeIdAndId(Long stocktakeId, Long scanId);

    List<FeltStocktakeScan> findByStocktakeId(Long stocktakeId);

    List<FeltStocktakeScan> findByStocktakeIdAndScannedStorageId(Long stocktakeId, Long storageId);

}
