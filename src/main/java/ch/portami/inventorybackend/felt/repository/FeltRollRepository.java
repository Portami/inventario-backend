package ch.portami.inventorybackend.felt.repository;

import ch.portami.inventorybackend.felt.entity.FeltRoll;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeltRollRepository extends JpaRepository<FeltRoll, Long> {
}