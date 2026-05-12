package ch.portami.inventorybackend.felt.supply.repository;

import ch.portami.inventorybackend.felt.supply.entity.Supply;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplyRepository extends JpaRepository<Supply, Long> {

    List<Supply> findAllByIsLowOnSupplyTrueOrHasBeenReorderedTrue();
}
