package ch.portami.inventorybackend.cutassistant.domain;

/**
 * Represents a single piece to be cut as part of a larger {@link CutProposal}.
 * It includes the dimensions of the piece and identifies the source stock material.
 */
public class ProposedCut {

    private double width;
    private double height;
    private String sourceStockId; // E.g., a roll or a remnant ID

    // Constructors, Getters, and Setters
}
