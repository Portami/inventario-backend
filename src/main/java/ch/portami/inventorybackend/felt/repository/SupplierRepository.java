package ch.portami.inventorybackend.felt.repository;

import ch.portami.inventorybackend.felt.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

}