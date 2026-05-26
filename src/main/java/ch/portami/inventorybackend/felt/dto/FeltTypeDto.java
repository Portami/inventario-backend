package ch.portami.inventorybackend.felt.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A felt type representing the material category of felt used for inventory items.")
public record FeltTypeDto(
        @Schema(description = "Felt type ID.") Long id,

        @Schema(description = "Felt type name (e.g. 'Wool', 'Synthetic').") String name) {

}
