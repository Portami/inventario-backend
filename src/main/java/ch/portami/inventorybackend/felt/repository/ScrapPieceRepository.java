package ch.portami.inventorybackend.felt.repository;

import ch.portami.inventorybackend.felt.entity.ScrapPiece;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapPieceRepository extends JpaRepository<ScrapPiece, Long> {

    List<ScrapPiece> findByFeltId(Long feltId);
}
