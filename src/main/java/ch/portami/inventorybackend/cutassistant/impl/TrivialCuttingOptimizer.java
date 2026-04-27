package ch.portami.inventorybackend.cutassistant.impl;

import ch.portami.inventorybackend.cutassistant.CuttingOptimizer;
import ch.portami.inventorybackend.cutassistant.CuttingStockLoader;
import ch.portami.inventorybackend.cutassistant.domain.AssignedPiece;
import ch.portami.inventorybackend.cutassistant.domain.CutInput;
import ch.portami.inventorybackend.cutassistant.domain.CutResult;
import ch.portami.inventorybackend.cutassistant.domain.CuttableStock;
import ch.portami.inventorybackend.cutassistant.domain.CuttingAssignment;
import ch.portami.inventorybackend.cutassistant.domain.RequiredPiece;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * A trivial first-fit implementation: assigns each required piece to the first matching stock item (matching
 * feltVariantId and color and dimensions large enough). Each stock item is used at most once.
 */
@Service
public class TrivialCuttingOptimizer implements CuttingOptimizer {

    private final CuttingStockLoader stockLoader;

    public TrivialCuttingOptimizer(CuttingStockLoader stockLoader) {
        this.stockLoader = stockLoader;
    }

    @Override
    public CutResult optimize(CutInput input) {
        List<CuttableStock> stocks = stockLoader.loadAll();
        List<CuttingAssignment> assignments = new ArrayList<>();
        Set<Long> usedStock = new HashSet<>();
        double totalWaste = 0.0;

        for (RequiredPiece req : input.requiredPieces()) {
            for (int q = 0; q < req.quantity(); q++) {
                for (CuttableStock s : stocks) {
                    // PORTAMI-57 check validation requirements (especially with usedStock)
                    if (usedStock.contains(s.feltColorVariantId())
                            || !matchesVariantAndColor(s, req)
                            || s.length() < req.length()
                            || s.width() < req.width()) {
                        continue;
                    }

                    AssignedPiece ap = new AssignedPiece(
                            req.feltVariantId(),
                            req.color(),
                            req.length(),
                            req.width()
                    );

                    List<AssignedPiece> list = new ArrayList<>();
                    list.add(ap);

                    // PORTAMI-57 do proper calculation of waste in separate method
                    double waste = (s.length() * s.width()) - (req.length() * req.width());
                    totalWaste += waste;

                    assignments.add(new CuttingAssignment(s, list, waste));
                    // PORTAMI-57 check for when a stock is properly 'used'
                    usedStock.add(s.feltColorVariantId());

                    return new CutResult(assignments, totalWaste, true);
                }
            }
        }

        return new CutResult(assignments, totalWaste, false);
    }

    private boolean matchesVariantAndColor(CuttableStock s, RequiredPiece req) {
        if (!s.feltVariantId().equals(req.feltVariantId())) {
            return false;
        }

        if (req.color() == null) {
            return true;
        }

        return req.color().equalsIgnoreCase(s.color());
    }
}

