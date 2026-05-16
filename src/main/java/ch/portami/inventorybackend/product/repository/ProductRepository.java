package ch.portami.inventorybackend.product.repository;

import ch.portami.inventorybackend.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}