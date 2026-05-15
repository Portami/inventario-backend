package ch.portami.inventorybackend.felt.repository;

import ch.portami.inventorybackend.felt.entity.FeltRoll;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeltRollRepository extends JpaRepository<FeltRoll, Long> {

    List<FeltRoll> findByFeltId(Long feltId);
}
