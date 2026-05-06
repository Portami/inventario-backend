package ch.portami.inventorybackend.restocking.repository;

import ch.portami.inventorybackend.restocking.entity.Supply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplyRepository extends JpaRepository<Supply, Long> {

}
