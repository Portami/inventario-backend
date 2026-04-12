package ch.portami.inventorybackend.felt.repository;

import ch.portami.inventorybackend.felt.entity.ScrapPiece;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapPieceRepository extends JpaRepository<ScrapPiece, Long> {
}