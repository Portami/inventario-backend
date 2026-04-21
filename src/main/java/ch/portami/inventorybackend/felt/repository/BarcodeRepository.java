package ch.portami.inventorybackend.felt.repository;

import ch.portami.inventorybackend.felt.entity.Barcode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BarcodeRepository extends JpaRepository<Barcode, Long> {
}
