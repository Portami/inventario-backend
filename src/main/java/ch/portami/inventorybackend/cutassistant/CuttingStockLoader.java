package ch.portami.inventorybackend.cutassistant;

import ch.portami.inventorybackend.cutassistant.domain.CuttableStock;
import ch.portami.inventorybackend.cutassistant.domain.StockType;
import ch.portami.inventorybackend.felt.entity.FeltColorVariant;
import ch.portami.inventorybackend.felt.entity.FeltRoll;
import ch.portami.inventorybackend.felt.entity.ScrapPiece;
import ch.portami.inventorybackend.felt.repository.FeltRollRepository;
import ch.portami.inventorybackend.felt.repository.ScrapPieceRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CuttingStockLoader {

    private final FeltRollRepository feltRollRepository;
    private final ScrapPieceRepository scrapPieceRepository;

    public CuttingStockLoader(FeltRollRepository feltRollRepository, ScrapPieceRepository scrapPieceRepository) {
        this.feltRollRepository = feltRollRepository;
        this.scrapPieceRepository = scrapPieceRepository;
    }

    @Transactional
    public List<CuttableStock> loadAll() {
        List<CuttableStock> result = new ArrayList<>();

        for (FeltRoll r : feltRollRepository.findAll()) {
            FeltColorVariant feltColorVariant = r.getFeltColorVariant();

            if (feltColorVariant == null) {
                continue;
            }

            result.add(new CuttableStock(
                    StockType.ROLL,
                    feltColorVariant.getFeltVariant().getId(),
                    feltColorVariant.getId(),
                    feltColorVariant.getColor(),
                    r.getLength(),
                    r.getWidth())
            );
        }

        for (ScrapPiece s : scrapPieceRepository.findAll()) {
            FeltColorVariant feltColorVariant = s.getFeltColorVariant();

            if (feltColorVariant == null) {
                continue;
            }

            result.add(new CuttableStock(
                    StockType.SCRAP,
                    feltColorVariant.getFeltVariant().getId(),
                    feltColorVariant.getId(),
                    feltColorVariant.getColor(),
                    s.getLength(),
                    s.getWidth())
            );
        }

        return result;
    }
}


