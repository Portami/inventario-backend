package ch.portami.inventorybackend.cutassistant.api.dto;

import java.util.List;

/**
 * DTO for requesting new cut proposals for a specific felt type.
 */
public record RequestCutProposalsDto(
    String feltType,
    List<RequestedPieceDto> requestedPieces
) {
    /**
     * A single requested piece with its dimensions and quantity.
     */
    public record RequestedPieceDto(
        double width,
        double height,
        int quantity
    ) {}
}
