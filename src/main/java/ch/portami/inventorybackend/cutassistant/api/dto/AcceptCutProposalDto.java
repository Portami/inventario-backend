package ch.portami.inventorybackend.cutassistant.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for accepting a specific cut proposal.
 */
public record AcceptCutProposalDto(
    @NotBlank(message = "proposalId must not be blank") String proposalId
) {}
