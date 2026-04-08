package ch.portami.inventorybackend.product.repository;

import ch.portami.inventorybackend.product.entity.ProductAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductAttributeValueRepository extends JpaRepository<ProductAttributeValue, Long> {
    List<ProductAttributeValue> findByProductVariant_Id(Long productVariantId);
    List<ProductAttributeValue> findByProduct_Id(Long productId);
}