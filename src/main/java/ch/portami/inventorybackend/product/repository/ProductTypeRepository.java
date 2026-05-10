package ch.portami.inventorybackend.product.repository;

import ch.portami.inventorybackend.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTypeRepository extends JpaRepository<Category, Long> {

}