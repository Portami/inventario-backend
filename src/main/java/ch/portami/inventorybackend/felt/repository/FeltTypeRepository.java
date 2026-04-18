package ch.portami.inventorybackend.felt.repository;

import ch.portami.inventorybackend.felt.entity.FeltType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeltTypeRepository extends JpaRepository<FeltType, Long> {
}