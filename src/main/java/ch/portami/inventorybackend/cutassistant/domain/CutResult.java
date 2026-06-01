package ch.portami.inventorybackend.cutassistant.domain;

import java.util.List;

/**
 * The result of a cutting optimization run.
 *
 * @param assignments the per-stock-item assignments of required pieces
 * @param totalWaste  the total leftover material across all assignments
 * @param feasible    whether all required pieces could be placed
 * @param reason      an explanation when the result is not feasible, otherwise {@code null}
 */
public record CutResult(List<CuttingAssignment> assignments, Double totalWaste, Boolean feasible, String reason) {

}

