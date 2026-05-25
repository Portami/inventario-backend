package ch.portami.inventorybackend.cutassistant.domain;

import java.util.List;

/**
 * Represents a single, complete proposal for cutting a set of requested pieces
 * from available stock, optimized for minimal waste.
 */
public class CutProposal {

    private String proposalId;
    private List<ProposedCut> proposedCuts;
    private double totalWaste;
    private double efficiency;

    // Constructors, Getters, and Setters
}
