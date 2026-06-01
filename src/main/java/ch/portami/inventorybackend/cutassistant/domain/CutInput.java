package ch.portami.inventorybackend.cutassistant.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * The request to the cutting optimizer: the set of pieces that need to be cut.
 *
 * @param requiredPieces the pieces to cut; must not be empty
 */
public record CutInput(
        @NotEmpty(message = "requiredPieces must not be empty") @Valid List<RequiredPiece> requiredPieces) {

}

