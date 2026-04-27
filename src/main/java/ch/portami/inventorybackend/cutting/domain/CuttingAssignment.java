package ch.portami.inventorybackend.cutting.domain;

import java.util.List;

public record CuttingAssignment(CuttableStock stockItem, List<AssignedPiece> pieces, Double waste) {

}

