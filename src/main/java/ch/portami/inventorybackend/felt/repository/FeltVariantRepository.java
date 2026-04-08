package ch.portami.inventorybackend.felt.repository;

import ch.portami.inventorybackend.felt.entity.FeltVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeltVariantRepository extends JpaRepository<FeltVariant, Long> {
    List<FeltVariant> findByFelt_Id(Long feltId);
}