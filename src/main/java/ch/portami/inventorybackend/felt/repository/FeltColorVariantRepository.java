package ch.portami.inventorybackend.felt.repository;

import ch.portami.inventorybackend.felt.entity.FeltColorVariant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeltColorVariantRepository extends JpaRepository<FeltColorVariant, Long> {

    List<FeltColorVariant> findByFeltVariantId(Long feltVariantId);
}