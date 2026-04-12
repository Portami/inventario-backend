package ch.portami.inventorybackend.product.repository;

import ch.portami.inventorybackend.product.entity.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {
}