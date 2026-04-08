package ch.portami.inventorybackend.product.repository;

import ch.portami.inventorybackend.product.entity.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long> {
    List<ProductInventory> findByProductVariant_Id(Long productVariantId);
    List<ProductInventory> findByStorage_Id(Long storageId);
}