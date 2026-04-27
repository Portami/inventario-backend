package ch.portami.inventorybackend.cutassistant;

import ch.portami.inventorybackend.cutassistant.domain.CutInput;
import ch.portami.inventorybackend.cutassistant.domain.CutResult;

/**
 * Algorithm interface for cutting optimization.
 */
public interface CuttingOptimizer {

    /**
     * Run the optimizer for the given input and return a CutResult describing assignments and waste.
     */
    CutResult optimize(CutInput input);
}


