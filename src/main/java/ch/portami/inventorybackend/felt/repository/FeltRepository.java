package ch.portami.inventorybackend.felt.repository;

import ch.portami.inventorybackend.felt.entity.Felt;
import ch.portami.inventorybackend.felt.entity.FeltType;
import ch.portami.inventorybackend.felt.entity.Supplier;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeltRepository extends JpaRepository<Felt, Long> {

    Optional<Felt> findByFeltTypeAndSupplierAndArticleNumber(
            FeltType feltType, Supplier supplier, String articleNumber);

    Optional<Felt> findByArticleNumber(String articleNumber);
}