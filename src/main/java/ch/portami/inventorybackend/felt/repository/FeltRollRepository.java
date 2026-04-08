package ch.portami.inventorybackend.felt.repository;

import ch.portami.inventorybackend.felt.entity.FeltRoll;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeltRollRepository extends JpaRepository<FeltRoll, Long> {
    List<FeltRoll> findByStorage_Id(Long storageId);
    List<FeltRoll> findByBatch_Id(Long batchId);
    List<FeltRoll> findByIsMainRollTrue();
}