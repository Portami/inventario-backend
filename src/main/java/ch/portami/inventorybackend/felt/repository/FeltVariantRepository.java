package ch.portami.inventorybackend.felt.repository;

import ch.portami.inventorybackend.felt.entity.FeltVariant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeltVariantRepository extends JpaRepository<FeltVariant, Long> {

    List<FeltVariant> findByFeltId(Long feltId);
}