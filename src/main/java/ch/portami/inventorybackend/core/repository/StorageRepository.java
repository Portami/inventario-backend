package ch.portami.inventorybackend.core.repository;

import ch.portami.inventorybackend.core.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageRepository extends JpaRepository<Storage, Long> {
}