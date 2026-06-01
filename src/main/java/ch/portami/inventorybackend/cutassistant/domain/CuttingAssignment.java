package ch.portami.inventorybackend.cutassistant.domain;

import java.util.List;

/**
 * The pieces assigned to a single stock item by the optimizer, with the resulting waste.
 *
 * @param stockItem the stock item the pieces are cut from
 * @param pieces    the pieces assigned to this stock item
 * @param waste     the leftover material on this stock item after the cuts
 */
public record CuttingAssignment(CuttableStock stockItem, List<AssignedPiece> pieces, Double waste) {

}

