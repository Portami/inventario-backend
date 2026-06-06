package ch.portami.inventorybackend.cutassistant;

import ch.portami.inventorybackend.cutassistant.domain.CuttableStock;
import ch.portami.inventorybackend.cutassistant.domain.StockType;
import ch.portami.inventorybackend.felt.entity.Felt;
import ch.portami.inventorybackend.felt.entity.FeltRoll;
import ch.portami.inventorybackend.felt.entity.ScrapPiece;
import ch.portami.inventorybackend.felt.repository.FeltRollRepository;
import ch.portami.inventorybackend.felt.repository.ScrapPieceRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Loads the available cuttable stock for the optimizer by collecting all felt rolls and scrap
 * pieces and adapting them into {@link CuttableStock} items.
 */
@Service
public class CuttingStockLoader {

    private final FeltRollRepository feltRollRepository;
    private final ScrapPieceRepository scrapPieceRepository;

    public CuttingStockLoader(FeltRollRepository feltRollRepository, ScrapPieceRepository scrapPieceRepository) {
        this.feltRollRepository = feltRollRepository;
        this.scrapPieceRepository = scrapPieceRepository;
    }

    /**
     * Loads all felt rolls and scrap pieces as cuttable stock. Items whose felt is unknown are
     * skipped.
     *
     * @return the available stock, with rolls and scrap pieces combined into one list
     */
    @Transactional
    public List<CuttableStock> loadAll() {
        List<CuttableStock> result = new ArrayList<>();

        for (FeltRoll r : feltRollRepository.findAll()) {
            Felt felt = r.getFelt();

            if (felt == null) {
                continue;
            }

            result.add(new CuttableStock(
                    StockType.ROLL,
                    felt.getId(),
                    felt.getColor(),
                    r.getLength(),
                    r.getWidth())
            );
        }

        for (ScrapPiece s : scrapPieceRepository.findAll()) {
            Felt felt = s.getFelt();

            if (felt == null) {
                continue;
            }

            result.add(new CuttableStock(
                    StockType.SCRAP,
                    felt.getId(),
                    felt.getColor(),
                    s.getLength(),
                    s.getWidth())
            );
        }

        return result;
    }
}
