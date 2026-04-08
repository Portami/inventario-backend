package ch.portami.inventorybackend.felt.repository;

import ch.portami.inventorybackend.felt.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchRepository extends JpaRepository<Batch, Long> {
}