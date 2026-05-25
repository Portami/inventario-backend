package ch.portami.inventorybackend.cutassistant.api.dto;

import java.util.List;

/**
 * DTO representing a single, complete proposal for cutting pieces.
 */
public record CutProposalDto(
    String proposalId,
    List<ProposedCutDto> proposedCuts,
    double totalWaste
) {
    /**
     * DTO for a single cut piece within a proposal.
     */
    public record ProposedCutDto(
        double width,
        double height,
        String sourceStockId
    ) {}
}
