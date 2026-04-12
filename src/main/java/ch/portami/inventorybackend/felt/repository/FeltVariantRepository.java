package ch.portami.inventorybackend.felt.repository;

import ch.portami.inventorybackend.felt.entity.FeltVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeltVariantRepository extends JpaRepository<FeltVariant, Long> {
}