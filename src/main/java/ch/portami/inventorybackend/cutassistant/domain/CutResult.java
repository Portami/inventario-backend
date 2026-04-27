package ch.portami.inventorybackend.cutassistant.domain;

import java.util.List;

public record CutResult(List<CuttingAssignment> assignments, Double totalWaste, Boolean feasible) {

}

