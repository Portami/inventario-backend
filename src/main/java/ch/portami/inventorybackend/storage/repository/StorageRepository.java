package ch.portami.inventorybackend.storage.repository;

import ch.portami.inventorybackend.storage.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageRepository extends JpaRepository<Storage, Long> {

}
