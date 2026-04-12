package ch.portami.inventorybackend.product.repository;

import ch.portami.inventorybackend.product.entity.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long> {
}