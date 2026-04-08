package ch.portami.inventorybackend.product;

import ch.portami.inventorybackend.product.model.Color;
import ch.portami.inventorybackend.product.model.Product;
import ch.portami.inventorybackend.product.model.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByType(ProductType type);
    List<Product> findByColor(Color color);
    List<Product> findByTypeAndColor(ProductType type, Color color);
}