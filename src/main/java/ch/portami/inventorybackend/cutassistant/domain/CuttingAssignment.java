package ch.portami.inventorybackend.cutassistant.domain;

import java.util.List;

public record CuttingAssignment(CuttableStock stockItem, List<AssignedPiece> pieces, Double waste) {

}

