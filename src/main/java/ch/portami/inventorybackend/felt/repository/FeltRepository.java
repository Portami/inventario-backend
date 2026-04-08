package ch.portami.inventorybackend.felt.repository;

import ch.portami.inventorybackend.felt.entity.Felt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeltRepository extends JpaRepository<Felt, Long> {
    List<Felt> findByFeltType_Id(Long feltTypeId);
    List<Felt> findBySupplier_Id(Long supplierId);
}