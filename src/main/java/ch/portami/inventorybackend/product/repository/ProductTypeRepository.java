package ch.portami.inventorybackend.product.repository;

import ch.portami.inventorybackend.product.entity.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {
}