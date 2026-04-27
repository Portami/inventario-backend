package ch.portami.inventorybackend.cutting.domain;

import java.util.List;

public record CutResult(List<CuttingAssignment> assignments, Double totalWaste, Boolean feasible) {

}

