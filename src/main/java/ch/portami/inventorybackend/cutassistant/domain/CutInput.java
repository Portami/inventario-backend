package ch.portami.inventorybackend.cutassistant.domain;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record CutInput(@NotEmpty(message = "requiredPieces must not be empty") @Valid List<RequiredPiece> requiredPieces) {

}

