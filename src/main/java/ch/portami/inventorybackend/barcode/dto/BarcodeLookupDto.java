package ch.portami.inventorybackend.barcode.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result of resolving a scanned barcode to the domain entity it identifies.")
public record BarcodeLookupDto(
        @Schema(description = "Kind of entity the barcode is attached to.", allowableValues = {"roll",
                "scrap"}, example = "roll") String type,

        @Schema(description = "Primary key of the linked entity (FeltRoll ID when type is 'roll', ScrapPiece ID when type is 'scrap'). Use this to fetch the full record via /api/rolls/{id} or the scrap-piece endpoint.", example = "42") Long id) {

}
