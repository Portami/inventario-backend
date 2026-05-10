package ch.portami.inventorybackend.product.repository;

import ch.portami.inventorybackend.product.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

}