package ch.portami.inventorybackend.barcode.repository;

import ch.portami.inventorybackend.barcode.entity.Barcode;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BarcodeRepository extends JpaRepository<Barcode, Long> {

    Optional<Barcode> findByScrapPieceId(Long scrapPieceId);
}
