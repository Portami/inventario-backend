package ch.portami.inventorybackend.felt.repository;

import ch.portami.inventorybackend.felt.entity.FeltColorVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeltColorVariantRepository extends JpaRepository<FeltColorVariant, Long> {
    List<FeltColorVariant> findByFeltVariant_Id(Long feltVariantId);
}