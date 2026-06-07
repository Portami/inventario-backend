package ch.portami.inventorybackend.felt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Result of an "Abschneiden" operation: the shortened roll plus the scrap pieces that were actually
 * created (too-small pieces from the request are dropped and therefore absent here).
 *
 * @param roll          the roll after its length was reduced
 * @param createdScraps the scrap pieces that were kept and persisted
 */
@Schema(description = "Outcome of a roll cut: the shortened roll and the scrap pieces that were actually kept.")
public record CutResultDto(
        @Schema(description = "The roll after shortening; still a roll.")
        FeltRollDto roll,

        @Schema(description = "Scrap pieces created from the cut-off material (too-small pieces are omitted).")
        List<ScrapPieceDto> createdScraps
) {

}
