package ch.portami.inventorybackend.stocktake.felt.repository;

import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeltStocktakeItemRepository extends JpaRepository<FeltStocktakeItem, Long> {

    List<FeltStocktakeItem> findByStocktakeId(Long stocktakeId);

    Optional<FeltStocktakeItem> findByStocktakeIdAndId(Long stocktakeId, Long itemId);

    @Query("SELECT i FROM FeltStocktakeItem i " +
            "INNER JOIN FeltStocktakeRollOrScrap r ON r.stocktakeItem.id = i.id " +
            "WHERE i.stocktakeId = :stocktakeId AND r.roll.id = :rollId")
    Optional<FeltStocktakeItem> findByStocktakeIdAndRollId(Long stocktakeId, Long rollId);

    @Query("SELECT i FROM FeltStocktakeItem i " +
            "INNER JOIN FeltStocktakeRollOrScrap r ON r.stocktakeItem.id = i.id " +
            "WHERE i.stocktakeId = :stocktakeId AND r.scrap.id = :scrapId")
    Optional<FeltStocktakeItem> findByStocktakeIdAndScrapId(Long stocktakeId, Long scrapId);

    Optional<FeltStocktakeItem> findByStocktakeIdAndBarcode(Long stocktakeId, String barcode);
    
}
