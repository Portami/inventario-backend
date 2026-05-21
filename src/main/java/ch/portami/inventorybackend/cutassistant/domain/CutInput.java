package ch.portami.inventorybackend.cutassistant.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CutInput(
        @NotEmpty(message = "requiredPieces must not be empty") @Valid List<RequiredPiece> requiredPieces) {

}

