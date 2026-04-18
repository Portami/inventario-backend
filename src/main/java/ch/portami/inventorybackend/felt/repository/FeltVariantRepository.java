package ch.portami.inventorybackend.felt.repository;

import ch.portami.inventorybackend.felt.entity.Felt;
import ch.portami.inventorybackend.felt.entity.FeltVariant;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeltVariantRepository extends JpaRepository<FeltVariant, Long> {

    Optional<FeltVariant> findByFeltAndThicknessAndDensityAndPrice(
            Felt felt, Double thickness, Double density, BigDecimal price);
}