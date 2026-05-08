package ch.portami.inventorybackend.core.storage.repository;

import ch.portami.inventorybackend.core.storage.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageRepository extends JpaRepository<Storage, Long> {
}