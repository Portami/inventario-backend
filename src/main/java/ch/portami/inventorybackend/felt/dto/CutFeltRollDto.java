package ch.portami.inventorybackend.felt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * Request body for the "Abschneiden" operation: shorten a roll's length and keep the resulting
 * leftover scrap pieces. The roll always stays a roll; the usable product cut for an order is not
 * tracked and is therefore not part of this request.
 *
 * @param cutLength the length, in centimetres, to remove from the roll; must be positive and less
 *                  than the roll's current length
 * @param scraps    the leftover scrap pieces to keep; may be null or empty. Pieces below the
 *                  configured minimum size are silently dropped.
 */
@Schema(description = "Shortens a roll by cutLength and creates the leftover scrap pieces to keep.")
public record CutFeltRollDto(
        @Schema(description = "Length in centimetres to cut off the roll.", example = "50.0")
        @NotNull @Positive Double cutLength,

        @Schema(description = "Leftover scrap pieces to keep from the cut-off material.", nullable = true)
        @Valid List<CutScrapDto> scraps
) {

}
