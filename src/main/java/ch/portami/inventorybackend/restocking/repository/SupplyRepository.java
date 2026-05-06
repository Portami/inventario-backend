package ch.portami.inventorybackend.restocking.repository;

import ch.portami.inventorybackend.restocking.entity.Supply;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplyRepository extends JpaRepository<Supply, Long> {

    List<Supply> findAllByIsLowOnSupplyTrueOrHasBeenReorderedTrue();
}
