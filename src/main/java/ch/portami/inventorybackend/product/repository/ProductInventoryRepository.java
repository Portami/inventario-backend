package ch.portami.inventorybackend.product.repository;

import ch.portami.inventorybackend.product.entity.ProductInventory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long> {

    Optional<ProductInventory> findByProductVariantIdAndStorageId(Long productVariantId, Long storageId);

}