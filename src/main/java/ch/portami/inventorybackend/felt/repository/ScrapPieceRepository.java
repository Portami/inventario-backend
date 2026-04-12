package ch.portami.inventorybackend.felt.repository;

import ch.portami.inventorybackend.felt.entity.ScrapPiece;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScrapPieceRepository extends JpaRepository<ScrapPiece, Long> {
}