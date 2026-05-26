package ch.portami.inventorybackend.stocktake.felt.repository;

import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktake;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeltStocktakeRepository extends JpaRepository<FeltStocktake, Long> {

}
