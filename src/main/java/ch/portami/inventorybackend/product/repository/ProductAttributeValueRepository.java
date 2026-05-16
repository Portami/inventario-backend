package ch.portami.inventorybackend.product.repository;

import ch.portami.inventorybackend.product.entity.ProductAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductAttributeValueRepository extends JpaRepository<ProductAttributeValue, Long> {

}